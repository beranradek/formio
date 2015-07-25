/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.render;

import net.formio.Field;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.props.FormElementProperty;
import net.formio.props.types.ButtonType;

/**
 * {@link FormRenderer} that renders also the whole HTML page or whole form tag
 * for debug purposes.
 * 
 * @author Radek Beran
 */
public class WholeFormRenderer extends FormRendererWrapper {
	
	public WholeFormRenderer(FormRenderer wrapped) {
		super(wrapped);
	}
	
	public <T> String renderHtmlFormPage(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>" + newLine());
		sb.append("<html lang=\"en\">" + newLine());
		sb.append("<head>" + newLine());
		sb.append("<meta charset=\"utf-8\">" + newLine());
		sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" + newLine());
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + newLine());
		sb.append("<title>Form rendering test</title>" + newLine());

		// Bootstrap CSS and JavaScript
		sb.append("<!-- Latest compiled and minified CSS -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css\">" + newLine());

		sb.append("<!-- JQuery UI for datepicker -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/ui-lightness/jquery-ui.min.css\">");
		sb.append("<!-- Optional theme -->" + newLine());
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css\">" + newLine());

		sb.append("<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->" + newLine());
		sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>" + newLine());
		sb.append("<!-- jQuery UI -->" + newLine());
		sb.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js\"></script>" + newLine());
		sb.append("<!-- Latest compiled and minified JavaScript -->" + newLine());
		sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js\"></script>" + newLine());

		sb.append("<script>" + newLine());
		sb.append("$(function(){" + newLine());
		sb.append("	$.datepicker.setDefaults(" + newLine());
		sb.append("	  $.extend($.datepicker.regional[''])" + newLine());
		sb.append(");" + newLine());
		sb.append("});" + newLine());
		sb.append("</script>" + newLine());
		
		sb.append("<style>" + newLine());
		sb.append(".field-label, .mapping-label { text-align: right; }" + newLine());
		sb.append("</style>" + newLine());

		sb.append("</head>" + newLine());
		sb.append("<body style=\"margin:1em\">" + newLine());

		sb.append(renderHtmlForm(formMapping));

		sb.append("</body>" + newLine());
		sb.append("</html>" + newLine());
		return sb.toString();
	}

	public <T> String renderHtmlForm(FormMapping<T> formMapping) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"" + getRenderContext().getActionUrl() + 
			"\" method=\"" + getRenderContext().getMethod().name() + 
			"\" role=\"form\">" + newLine());
		if (formMapping.isVisible()) {
			sb.append(renderMarkupGlobalMessages(formMapping));
			sb.append(renderElement(formMapping));
			if (!containsSubmitButton(formMapping)) {
				sb.append(renderDefaultSubmitButton());
			}
		}
		sb.append("</form>" + newLine());
		return sb.toString();
	}
	
	protected <T> boolean containsSubmitButton(FormMapping<T> mapping) {
		// searching only on top level
		for (FormField<?> field : mapping.getFields().values()) {
			if (Field.BUTTON.getType().equals(field.getType())) {
				ButtonType buttonType = field.getProperties().getProperty(FormElementProperty.BUTTON_TYPE);
				if (buttonType == null || buttonType == ButtonType.SUBMIT) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected String renderDefaultSubmitButton() {
		return renderFieldButton(Forms.<String> field(
			PROPERTY_DEFAULT_SUBMIT,
			Field.BUTTON.getType()).build());
	}
	
	private final String PROPERTY_DEFAULT_SUBMIT = "_defaultSubmitButton";
}
