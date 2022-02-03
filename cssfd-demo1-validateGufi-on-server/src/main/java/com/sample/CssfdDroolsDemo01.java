package com.sample;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample class to launch a flight plan validation rule.
 */
public class CssfdDroolsDemo01 {

	public static final void main(String[] args) {

		try { 

			Path fileName;
			String msg = "";
			// Send a Flight Plan with a VALID UUID in the GUFI field to Drools for validation
			fileName = Path.of(
					"C:\\Users\\Ram.Raju.CTR\\Documents\\cssfd\\data\\FF-ICE-Filed-Flight-Plan-Sample-ValidGufi.xml");
			System.out.println("Processing file: " + fileName);
			msg = Files.readString(fileName);
			validateMessage(msg);

			//Send a Flight Plan with an  IINVALID UUID in the GUFI field to Drools for validation
			fileName = Path.of(
					"C:\\Users\\Ram.Raju.CTR\\Documents\\cssfd\\data\\FF-ICE-Filed-Flight-Plan-Sample-InvalidGufi.xml");
			System.out.println("Processing file: " + fileName);
			msg = Files.readString(fileName);
			validateMessage(msg);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static void validateMessage(String msg) {

		try {
			// load up the Drools knowledge base environment
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieSession kSession = kContainer.newKieSession("ksession-rules");
        	
			// Extract the GUFI from the Flight Plan XML message
			String gufi = "";

			// Use JSoup to do the XML Parsing and extract the GUFI from the message
			Document doc = Jsoup.parse(msg, "", Parser.xmlParser());
			Elements withTypes = new Elements();

			for (Element element : doc.select("*")) {
				final String s[] = element.tagName().split(":");
				if (s.length > 1 && s[1].equals("gufi") == true)
					gufi = element.text();
			
			}

			System.out.println("GUFI is: " + gufi);

			// go !
			Message message = new Message();
			message.setMessage(msg);
			message.setGufi(gufi);
			kSession.insert(message);
			kSession.fireAllRules();
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	public static class Message {

		private String message;
		private int status;
		private String gufi;

		public static final int VALIDATE = 1;

		public String getMessage() {
			return this.message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getGufi() {
			return this.gufi;
		}

		public void setGufi(String gufi) {
			this.gufi = gufi;
		}

		
		public int getStatus() {
			return this.status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}

}

