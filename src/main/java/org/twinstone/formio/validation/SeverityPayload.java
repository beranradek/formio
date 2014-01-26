package org.twinstone.formio.validation;

import javax.validation.Payload;

/**
 * Severities that can be used as payloads in bean validation annotations to mark severity of validation.
 * @author Radek Beran
 */
public class SeverityPayload {
	public static class Info implements Payload { /** no members */ }
	public static class Warning implements Payload { /** no members */ }
	public static class Error implements Payload { /** no members */ }
}
