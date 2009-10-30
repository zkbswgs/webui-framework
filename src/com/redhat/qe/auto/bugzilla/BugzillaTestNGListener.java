package com.redhat.qe.auto.bugzilla;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.internal.IResultListener;

import com.redhat.qe.auto.testng.BzChecker;

public class BugzillaTestNGListener implements IResultListener{
	private static final String BLOCKED_BY_BUG = "blockedByBug";
	private static final String VERIFIES_BUG = "verifiesBug";
	protected static Logger log = Logger.getLogger(BugzillaTestNGListener.class.getName());
	protected static BzChecker bzChecker = null;

	@Override
	public void onConfigurationFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSkip(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestStart(ITestResult result) {
		/*
		 * if the test is in a group "blockedByBug-xxxxxx" and the bug is not in
		 * ON_QA, VERIFIED, RELEASE_PENDING, POST, CLOSED then skip it 
		 */
		if (bzChecker == null) {
			bzChecker = new BzChecker();
			bzChecker.init();
		}
		String[] groups = result.getMethod().getGroups();
		Pattern p = Pattern.compile(BLOCKED_BY_BUG + "-(\\d+)");
		for (String group: groups){
			Matcher m = p.matcher(group);
			if (m.find()){
				String number = m.group(1);
				BzChecker.bzState state = bzChecker.getBugState(number);
				if (! (state.equals(BzChecker.bzState.ON_QA) ||
						state.equals(BzChecker.bzState.VERIFIED) ||
						state.equals(BzChecker.bzState.RELEASE_PENDING) ||
						state.equals(BzChecker.bzState.POST) ||
						state.equals(BzChecker.bzState.CLOSED))){
					// the bug is not ready to retest
					throw new SkipException("This test is blocked by bz bug " + number + ", which is currently " + state.toString());
				}
			}			
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		if (bzChecker == null) {
			bzChecker = new BzChecker();
			bzChecker.init();
		}
		//if the test is in a group "verifiesBug-xxxxxx" and the bug is in ON_QA, close it
		String[] groups = result.getMethod().getGroups();
		Pattern p = Pattern.compile("[" + VERIFIES_BUG + "|" + BLOCKED_BY_BUG +"]-(\\d+)");
		for (String group: groups){
			Matcher m = p.matcher(group);
			if (m.find()){
				String number = m.group(1);
				BzChecker.bzState state = bzChecker.getBugState(number);
				if (group.startsWith(VERIFIES_BUG)) {
					log.fine("This test verifies bugzilla bug #"+ number);
					if (state.equals(BzChecker.bzState.ON_QA)){
						//TODO need to call code here to actually close the bug (doesn't work yet)
						log.warning("Need to verify bug " + number + "!");
						bzChecker.setBugState(number, BzChecker.bzState.VERIFIED);
						log.info("Verified bug " + number);
					}
					else log.warning("Bug " + number + " has been verified, but it is in " + state + " state instead of ON_QA");
				}
				else { //blockedByBug
					log.warning("Test is now unblocked by bug " + number + ".");
				}
			}
		}
	}
	
	public static void main (String... args) {
		Pattern p = Pattern.compile("[verifiesBug|blockedByBug]-(\\d+)");
		Matcher m = p.matcher("blockedByBug-12354542");
		m.find();
		System.out.println(m.group(1));
	}

}
