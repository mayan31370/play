package bran;

import java.lang.reflect.Type;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 
 */

public class ControllerActionMethodVisitor extends MethodVisitor implements Opcodes {
	/**
	 * 
	 */
	private final String owner;
	private MethodDescr methodDesc;

	public ControllerActionMethodVisitor(String owner, MethodDescr md, MethodVisitor mv) {
		super(ControllerClassVisitor.ASM5, mv);
		this.owner = owner;
		this.methodDesc = md;
	}

	@Override
	public void visitCode() {
		redirect();
		// alert();
	}

	private void alert() {
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("method called: " + owner + "." + methodDesc.name);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	private void redirect0() {
		mv.visitCode();

		mv.visitMethodInsn(INVOKESTATIC, "play/classloading/enhancers/ControllersEnhancer$ControllerInstrumentation",
				"isActionCallAllowed", "()Z", false);
		Label l0 = new Label();
		mv.visitJumpInsn(IFNE, l0);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getDeclaringClass", "()Ljava/lang/Class;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
		mv.visitLdcInsn(".");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
				"(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
				"(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "play/mvc/Controller", "redirect", "(Ljava/lang/String;[Ljava/lang/Object;)V",
				false);
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l0);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitMethodInsn(INVOKESTATIC, "play/classloading/enhancers/ControllersEnhancer$ControllerInstrumentation",
				"stopActionCall", "()V", false);
		mv.visitLabel(l1);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
	}

	private void redirect() {
		mv.visitCode();
		mv.visitMethodInsn(INVOKESTATIC, "play/classloading/enhancers/ControllersEnhancer$ControllerInstrumentation",
				"isActionCallAllowed", "()Z", false);
		Label l0 = new Label();
		mv.visitJumpInsn(IFNE, l0);
		useParamToCallAnother(this.methodDesc.paramTypes);
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l0);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitMethodInsn(INVOKESTATIC, "play/classloading/enhancers/ControllersEnhancer$ControllerInstrumentation",
				"stopActionCall", "()V", false);
		mv.visitLabel(l1);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
	}

	private void useParamToCallAnother(List<Type> pts) {
		mv.visitLdcInsn(owner.replace('/', '.') + "." + this.methodDesc.name);
		// parameter array
		int arrayLength = pts.size();
		mv.visitIntInsn(BIPUSH, arrayLength);
//		mv.visitInsn(ICONST_0);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

		int currentSlot = 0;
		for (int i = 0; i < pts.size(); i++) {
			mv.visitInsn(DUP);
			mv.visitIntInsn(BIPUSH, i);

			Type t = pts.get(i);
			// autoboxing
			if (t == int.class) {
				mv.visitVarInsn(ILOAD, currentSlot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(J)Ljava/lang/Integer;", false);
			} else if (t == long.class) {
				mv.visitVarInsn(LLOAD, currentSlot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			} else if (t == float.class) {
				mv.visitVarInsn(FLOAD, currentSlot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(J)Ljava/lang/Float;", false);
			} else if (t == double.class) {
				mv.visitVarInsn(DLOAD, currentSlot);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(J)Ljava/lang/Double;", false);
			} else
				mv.visitVarInsn(ALOAD, currentSlot);

			mv.visitInsn(AASTORE);

			currentSlot++;
			if (t == long.class || t == double.class) {
				currentSlot++;
			}

		}
		mv.visitMethodInsn(INVOKESTATIC, "play/mvc/Controller", "redirect", "(Ljava/lang/String;[Ljava/lang/Object;)V",
				false);
//		mv.visitInsn(RETURN);
	}

	@Override
	public void visitInsn(int opcode) {
		// if ((opcode >= ControllerClassVisitor.IRETURN && opcode <=
		// ControllerClassVisitor.RETURN) || opcode ==
		// ControllerClassVisitor.ATHROW) {
		// mv.visitFieldInsn(ControllerClassVisitor.GETSTATIC, owner, "timer",
		// "J");
		// mv.visitMethodInsn(ControllerClassVisitor.INVOKESTATIC,
		// "java/lang/System", "currentTimeMillis", "()J");
		// mv.visitInsn(ControllerClassVisitor.LADD);
		// mv.visitFieldInsn(ControllerClassVisitor.PUTSTATIC, owner, "timer",
		// "J");
		// }
		mv.visitInsn(opcode);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		mv.visitMaxs(maxStack + 3, maxLocals + 2); // should be ignored
	}
}