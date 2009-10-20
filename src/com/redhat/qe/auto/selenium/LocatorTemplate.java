package com.redhat.qe.auto.selenium;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A LocatorTemplate is used to define a functional selenium locator when creating an Element.
 * These are useful when the selenium locator depends on some argument(s) and is therefore not
 * a fixed value.  Example: xpath=//table/th/a[starts-with(.,'column name')]
 * In this example, the 'column name' is a functional argument that would be passed at
 * runtime to the getLocator("Name Column") method.
 * The constructors template value would be "xpath=//table/th/a[starts-with(.,'$1')]"
 * @author jsefler
 * 
 */
public class LocatorTemplate implements LocatorStrategy {

	protected String template = null;
	protected String name = null;
	private static Logger log = Logger.getLogger(ExtendedSelenium.class.getName());
	
	/**
	 * @param name - a brief human readable name for the template.
	 * 			Example: "link in resource breadcrumb trail"
	 * @param template - the locator path containing keywords of the form $d that will be sequentially replaced with the arguments passed to method getLocator(String... args).
	 * 			Example: "//span[normalize-space(.)='$1']/../a[normalize-space(.)='$2']"
	 */
	public LocatorTemplate(String name, String template){
		this.name = name;
		this.template = template;
	}
	
	/* (non-Javadoc)
	 * @see com.redhat.qe.auto.selenium.LocatorStrategy#getLocator(java.lang.String[])
	 */
	@Override
	public String getLocator(String... args) {
		
		int i=0;
		String locator = this.template;
		for (String arg : args) {
			locator = locator.replaceFirst("\\$"+(++i), arg);
		}
		
		// check for left over $d replacement strings
		if (locator.matches(".*\\$\\d.*")) {
			log.log(Level.FINE, "Template "+name+ " has left over replacement holders. ("+locator+")");
			//System.out.println("Template "+name+ " has left over replacement holders. ("+locator+")");
		}
		
		return locator;
	}

	/* (non-Javadoc)
	 * @see com.redhat.qe.auto.selenium.LocatorStrategy#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	public String getTemplate() {
		return this.template;
	}

	public static void main (String[] args){
		// this is just a developers test
		LocatorTemplate locatorTemplate = new LocatorTemplate("table column row#","//table[@id='$1']//th[$2]/a[starts-with(.,'$3')]/../../../tr[$4]/td[$5]");
		System.out.println(locatorTemplate.getLocator("table id1","10","column name","5","10"));
		System.out.println(locatorTemplate.getLocator("table id2"));
		System.out.println(locatorTemplate.getLocator("table id3","10","column name","5","10","too many args"));
		
	}
}
