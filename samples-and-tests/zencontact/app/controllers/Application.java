package controllers;

import java.util.Date;
import java.util.List;

import models.Contact;
import play.data.validation.Valid;
import cn.bran.play.JapidController;

public class Application extends JapidController {

    public static void index() {
        Date now = new Date();
        renderJapid(now);
    }
    
    public static void list() {
        List<Contact> contacts = Contact.find("order by name, firstname").fetch();
        renderJapid(contacts);
//        render(contacts);
        // the default template would be named list.html and the derived class name seems to be conflict to the List class
        // So I chain it to another action.
//        dontRedirect();
//        listAll(contacts);
    }
//    
//    public static void listAll(List<Contact> cs) {
//    	renderJapid(cs);
//    }
//    
    public static void form(Long id) {
        if(id == null) {
//            render();
        	renderJapid((Object)null);
        }
        Contact contact = Contact.findById(id);
//        render(contact);
        renderJapid(contact);
    }
    
    public static void save(@Valid Contact contact) {
        if(validation.hasErrors()) {
            if(request.isAjax()) 
            	error("Invalid value");
//            render("@form", contact);
            renderJapidWith("Application.form", contact);
        }
        System.out.println(contact.toString());
        contact.save();
        // redirect
        list();
    }

}