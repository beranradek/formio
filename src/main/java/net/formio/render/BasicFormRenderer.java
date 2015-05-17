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

import java.util.ArrayList;
import java.util.List;

import net.formio.BasicListFormMapping;
import net.formio.Field;
import net.formio.FormElement;
import net.formio.FormField;
import net.formio.FormMapping;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.action.HandledJsEvent;
import net.formio.choice.ChoiceRenderer;
import net.formio.common.MessageTranslator;
import net.formio.internal.FormUtils;
import net.formio.props.FormElementProperty;
import net.formio.validation.ConstraintViolationMessage;

/**
 * <p>Form renderer that is using Bootstrap markup and styles.
 * <p>You probably want to override the rendered markup to meet your needs - you
 * can create custom subclass that uses your favorite templating system and
 * overrides some or all methods with "renderMarkup" prefix.
 * <p>Thread-safe: Immutable.
 * 
 * @author Radek Beran
 */
public class BasicFormRenderer {

	private final RenderContext ctx;
	// Auxiliary renderers
	private final StyleRenderer styleRenderer;
	private final MessageRenderer messageRenderer;
	private final LabelRenderer labelRenderer;
	private final DatePickerRenderer datePickerRenderer;
	private final AjaxEventRenderer ajaxEventRenderer;

	public BasicFormRenderer(RenderContext ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("ctx cannot be null");
		}
		this.ctx = ctx;
		this.styleRenderer = new StyleRenderer(this);
		this.messageRenderer = new MessageRenderer(this);
		this.labelRenderer = new LabelRenderer(this, this.styleRenderer);
		this.datePickerRenderer = new DatePickerRenderer(this);
		this.ajaxEventRenderer = new AjaxEventRenderer(this);
	}

	/**
	 * <p>Renders given form element - form mapping or form field:</p>
	 * <ul>
	 * 	<li>Surrounding placeholder tag (even if the element is invisible).</li>
	 * 	<li>Form mapping or form field if it is visible (element markup).</li>
	 * </ul>
	 * <p>Visible mapping consists of mapping box with label and nested elements, 
	 * visible form field consists of form group with label and field envelope (with nested form input).</p>
	 * 
	 * @param element
	 * @return
	 */
	public <T> String renderElement(FormElement<T> element) {
		return renderMarkupElementPlaceholder(element, renderElementMarkup(element));
	}

	/**
	 * Renders the element itself without the surrounding placeholder tag.
	 * If the given element is invisible, returns empty string.
	 * @param element
	 * @return
	 */
	public <T> String renderElementMarkup(FormElement<T> element) {
		StringBuilder sb = new StringBuilder("");
		if (element.isVisible()) {
			sb.append(renderVisibleElement(element));
		}
		return sb.toString();
	}
		
	/**
	 * Renders element in visible state (assuming the element is visible).
	 * @param element
	 * @return
	 */
	public <T> String renderVisibleElement(FormElement<T> element) {
		String html = null;
		if (element instanceof FormMapping) {
			html = renderVisibleMapping((FormMapping<?>)element);
		} else if (element instanceof FormField) {
			html = renderVisibleField((FormField<?>)element);
		} else if (element != null) {
			throw new UnsupportedOperationException("Unsupported element " + element.getClass().getName());
		}
		return html;
	}

	/**
	 * Renders form mapping in visible state (assuming the mapping is visible).
	 * @param mapping
	 * @return
	 */
	public <T> String renderVisibleMapping(FormMapping<T> mapping) {
		StringBuilder sb = new StringBuilder();
		
		// Label
		sb.append(renderMarkupMappingLabel(mapping));

		// Mapping messages
		sb.append(renderMarkupMessageList(mapping));
		
		// Nested mappings and fields
		if (mapping instanceof BasicListFormMapping) {
			for (FormMapping<?> m : ((BasicListFormMapping<?>) mapping).getList()) {
				sb.append(renderElement(m));
			}
		} else {
			for (FormElement<?> el : mapping.getElements()) {
				sb.append(renderElement(el));
			}
		}
		return newLine() + renderMarkupMappingBox(mapping, sb.toString());
	}

	/**
	 * Renders form field in visible state (assuming the field is visible).
	 * @param field
	 * @return
	 */
	public <T> String renderVisibleField(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String type = field.getType();
		Field formComponent = Field.findByType(type);
		if (formComponent != null) {
			switch (formComponent) {
			case HIDDEN:
				sb.append(renderFieldHidden(field));
				break;
			case TEXT:
				sb.append(renderFieldText(field));
				break;
			case TEXT_AREA:
				sb.append(renderFieldTextArea(field));
				break;
			case PASSWORD:
				sb.append(renderFieldPassword(field));
				break;
			case CHECK_BOX:
				sb.append(renderFieldCheckbox(field));
				break;
			case DATE_PICKER:
				sb.append(renderFieldDatePicker(field));
				break;
			case DROP_DOWN_CHOICE:
				sb.append(renderFieldDropDownChoice(field));
				break;
			case FILE_UPLOAD:
				sb.append(renderFieldFileUpload(field));
				break;
			case MULTIPLE_CHECK_BOX:
				sb.append(renderFieldMultipleCheckbox(field));
				break;
			case RADIO_CHOICE:
				sb.append(renderFieldRadioChoice(field));
				break;
			case COLOR:
				sb.append(renderFieldColor(field));
				break;
			case DATE:
				sb.append(renderFieldDate(field));
				break;
			case DATE_TIME:
				sb.append(renderFieldDateTime(field));
				break;
			case DATE_TIME_LOCAL:
				sb.append(renderFieldDateTimeLocal(field));
				break;
			case TIME:
				sb.append(renderFieldTime(field));
				break;
			case EMAIL:
				sb.append(renderFieldEmail(field));
				break;
			case MONTH:
				sb.append(renderFieldMonth(field));
				break;
			case NUMBER:
				sb.append(renderFieldNumber(field));
				break;
			case RANGE:
				sb.append(renderFieldRange(field));
				break;
			case SEARCH:
				sb.append(renderFieldSearch(field));
				break;
			case TEL:
				sb.append(renderFieldTel(field));
				break;
			case URL:
				sb.append(renderFieldUrl(field));
				break;
			case WEEK:
				sb.append(renderFieldWeek(field));
				break;
			case SUBMIT_BUTTON:
				sb.append(renderFieldSubmitButton(field));
				break;
			case LINK:
				sb.append(renderFieldLink(field));
				break;
			default:
				throw new UnsupportedOperationException("Cannot render component with type " + type);
			}
		} else {
			throw new UnsupportedOperationException("Unsupported component with type " + type);
		}
		return sb.toString();
	}
	
	public <T> String renderMarkupGlobalMessages(FormMapping<T> formMapping) {
		return messageRenderer.renderGlobalMessages(formMapping);
	}

	/**
	 * Creates builder of AJAX response.
	 * @return
	 */
	public TdiResponseBuilder ajaxResponse() {
		return new TdiResponseBuilder(this);
	}
	
	protected <T> String renderMarkupElementPlaceholder(FormElement<T> element, String innerMarkup) {
		StringBuilder sb = new StringBuilder();
		// Element placeholder begin - rendered even for invisible element so there is reserved
		// identified place that can be updated if the element becomes visible.
		sb.append("<div id=\"" + element.getElementPlaceholderId() + "\">" + newLine());
		
		// The element itself
		sb.append(innerMarkup);
		
		// Element placeholder end
		sb.append("</div>" + newLine());
		return sb.toString();
	}

	protected <T> String renderMarkupMappingBox(FormMapping<T> mapping, String innerMarkup) {
		StringBuilder sb = new StringBuilder();
		String maxSevClass = getMaxSeverityClass(mapping);
		sb.append("<div class=\"" + maxSevClass + "\">" + newLine());
		sb.append(innerMarkup);
		sb.append("</div>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderMarkupFormGroup(FormField<T> field, String innerMarkup) {
		StringBuilder sb = new StringBuilder();
		String maxSevClass = getMaxSeverityClass(field);
		sb.append("<div class=\"" + styleRenderer.getFormGroupClasses() + " " + maxSevClass + "\">" + newLine());
		boolean checkbox = isCheckBox(field);
		if (checkbox) {
			sb.append("<div class=\"" + Field.CHECK_BOX.getInputType() + "\">" + newLine());
		}
		
		sb.append(innerMarkup);
		
		if (checkbox) {
			sb.append("</div>" + newLine());
		}
		sb.append("</div>" + newLine() + newLine());
		return sb.toString();
	}

	protected <T> String renderMarkupInputEnvelope(FormField<T> field, String innerMarkup) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"" + styleRenderer.getInputEnvelopeClasses(field) + "\">" + newLine());
		sb.append(innerMarkup);
		sb.append("</div>" + newLine());
		return sb.toString();
	}

	protected <T> String renderMarkupMessageList(FormElement<T> element) {
		return messageRenderer.renderMessageList(element);
	}

	protected <T> String renderMarkupMappingLabel(FormMapping<T> mapping) {
		return labelRenderer.renderMappingLabel(mapping);
	}

	protected <T> String renderMarkupFieldLabel(FormField<T> field) {
		return labelRenderer.renderFieldLabel(field);
	}
	
	protected <T> String renderMarkupTextArea(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea name=\"" + field.getName() + "\" id=\"" + field.getElementId() + 
			"\" class=\"" + getInputClasses(field) + "\"");
		sb.append(getElementAttributes(field));
		sb.append(getInputPlaceholderAttribute(field));
		sb.append(">");
		sb.append(escapeHtml(field.getValue()));
		sb.append("</textarea>" + newLine());
		sb.append(renderFieldScript(field, InputMultiplicity.SINGLE));
		return sb.toString();
	}

	protected <T> String renderMarkupInput(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		String typeId = field.getType();
		sb.append("<input type=\"" + field.getInputType() + "\" name=\"" + field.getName()
				+ "\" id=\"" + field.getElementId() + "\"");
		if (!Field.FILE_UPLOAD.getType().equals(typeId)) {
			String value = escapeHtml(field.getValue());
			sb.append(" value=\"" + value + "\"");
		}
		sb.append(getElementAttributes(field));
		sb.append(" class=\"" + getInputClasses(field) + "\"");
		sb.append(getInputPlaceholderAttribute(field));
		sb.append("/>" + newLine());
		sb.append(renderFieldScript(field, InputMultiplicity.SINGLE));
		return sb.toString();
	}

	protected <T> String renderMarkupCheckbox(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"" + Field.CHECK_BOX.getInputType() + "\" name=\"" + field.getName()
			+ "\" id=\"" + field.getElementId() + "\" value=\"1\"");
		if (field.isFilledWithTrue()) {
			sb.append(" checked=\"checked\" ");
		}
		sb.append(getElementAttributes(field));
		sb.append(" class=\"" + getInputClasses(field) + "\"");
		sb.append("/>" + newLine());
		sb.append(renderFieldScript(field, InputMultiplicity.SINGLE));
		return sb.toString();
	}

	protected <T> String renderMarkupSelect(FormField<T> field) {
		if (field.getChoiceRenderer() == null) {
			throw new IllegalStateException("Form field should have ChoiceRenderer defined");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + field.getName() + "\" id=\"" + field.getElementId() + "\"");
		Boolean multiple = field.getProperties().getProperty(FormElementProperty.MULTIPLE);
		if (multiple != null && multiple.booleanValue()) {
			sb.append(" multiple=\"multiple\"");
		}
		Integer size = field.getProperties().getProperty(FormElementProperty.SIZE);
		if (size != null) {
			sb.append(" size=\"" + size.intValue() + "\"");
		}
		sb.append(" class=\"" + getInputClasses(field) + "\"");
		sb.append(getElementAttributes(field));
		sb.append(">" + newLine());
		if (field.getChoices() != null) {
			List<T> items = toSimplyTypedItems(field.getChoices().getItems());
			if (items != null) {
				// First "Choose One" option
				if (field.getProperties().isChooseOptionDisplayed()) {
					sb.append(renderMarkupOption("", field.getProperties().getChooseOptionTitle(), false));
				}
				ChoiceRenderer<T> choiceRenderer = field.getChoiceRenderer();
				int itemIndex = 0;
				for (T item : items) {
					String value = getChoiceValue(choiceRenderer, item, itemIndex);
					String title = getChoiceTitle(choiceRenderer, item, itemIndex);
					boolean selected = field.getFilledObjects().contains(item);
					sb.append(renderMarkupOption(value, title, selected));
					itemIndex++;
				}
			}
		}
		sb.append("</select>" + newLine());
		sb.append(renderFieldScript(field, InputMultiplicity.SINGLE));
		return sb.toString();
	}

	protected <T> String renderMarkupChecks(FormField<T> field) {
		if (field.getChoiceRenderer() == null) {
			throw new IllegalStateException("Form field should have ChoiceRenderer defined");
		}
		StringBuilder sb = new StringBuilder();
		if (field.getChoices() != null) {
			List<T> items = toSimplyTypedItems(field.getChoices().getItems());
			if (items != null) {
				ChoiceRenderer<T> choiceRenderer = field.getChoiceRenderer();
				int itemIndex = 0;
				for (T item : items) {
					String value = getChoiceValue(choiceRenderer, item, itemIndex);
					String title = getChoiceTitle(choiceRenderer, item, itemIndex);
					String itemId = field.getElementIdWithIndex(itemIndex);

					sb.append("<div class=\"" + field.getInputType() + "\">" + newLine());
					if (field.getProperties().isLabelVisible()) {
						sb.append("<label>");
					}
					
					sb.append("<input type=\"" + field.getInputType() + "\" name=\"" + field.getName() + "\" id=\"" + itemId + "\" value=\"" + value + "\"");
					if (field.getFilledObjects().contains(item)) {
						sb.append(" checked=\"checked\"");
					}
					sb.append(getElementAttributes(field));
					sb.append(" class=\"" + getInputClasses(field) + "\"");
					sb.append("/>");
					if (field.getProperties().isLabelVisible()) {
						sb.append(" " + title + "</label>");
					}
					sb.append("</div>" + newLine());
					itemIndex++;
				}
				sb.append(renderFieldScript(field, InputMultiplicity.MULTIPLE));
			}
		}
		return sb.toString();
	}
	
	protected <T> String renderMarkupButton(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<button type=\"" + Field.SUBMIT_BUTTON.getInputType() + "\" value=\"" + escapeHtml(field.getValue()) + 
			"\" class=\"" + getInputClasses(field) + "\">");
		MessageTranslator tr = getMessageTranslator(field);
		String text = escapeHtml(tr.getMessage(field.getLabelKey()));
		sb.append(text);
		sb.append("</button>" + newLine());
		return sb.toString();
	}
	
	protected <T> String renderMarkupLink(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"" + escapeHtml(field.getValue()) + "\" class=\"" + getInputClasses(field) + "\">");
		MessageTranslator tr = getMessageTranslator(field);
		String text = escapeHtml(tr.getMessage(field.getLabelKey()));
		sb.append(text);
		sb.append("</a>" + newLine());
		sb.append(renderFieldScript(field, InputMultiplicity.SINGLE));
		return sb.toString();
	}

	protected <T> MessageTranslator getMessageTranslator(FormElement<T> element) {
		return RenderUtils.getMessageTranslator(element, getRenderContext().getLocale());
	}
	
	/**
	 * Returns string will all HTML attributes of given element. 
	 * @param element
	 * @return
	 */
	protected <T> String getElementAttributes(FormElement<T> element) {
		return getAccessibilityAttributes(element) + getAjaxAttributes(element);
	}

	protected <T> String getAccessibilityAttributes(FormElement<T> element) {
		StringBuilder sb = new StringBuilder();
		if (!element.isEnabled()) {
			sb.append(" disabled=\"disabled\"");
		}
		if (element.isReadonly()) {
			sb.append(" readonly=\"readonly\"");
		}
		return sb.toString();
	}

	/**
	 * Returns AJAX attributes of TDI library.
	 * @param element
	 * @return
	 */
	protected <T> String getAjaxAttributes(FormElement<T> element) {
		StringBuilder sb = new StringBuilder();
		if (element instanceof FormField) {
			FormField<?> field = (FormField<?>)element;
			HandledJsEvent ajaxActionWithoutEvent = field.getProperties().getDataAjaxActionWithoutEvent();
			if (ajaxActionWithoutEvent != null) {
				String url = FormUtils.urlWithAppendedParameter(ajaxActionWithoutEvent.getUrl(field.getParent().getConfig().getUrlBase(), field), 
					AjaxParams.SRC_ELEMENT_NAME, element.getName());
				sb.append(" data-ajax-url=\"" + url + "\"");
			}
			if (field.getProperties().getDataRelatedElement() != null && !field.getProperties().getDataRelatedElement().isEmpty()) {
				sb.append(" data-related-element=\"" + field.getProperties().getDataRelatedElement() + "\"");
			}
			if (field.getProperties().getDataRelatedAncestor() != null && !field.getProperties().getDataRelatedAncestor().isEmpty()) {
				sb.append(" data-related-ancestor=\"" + field.getProperties().getDataRelatedAncestor() + "\"");
			}
			if (field.getProperties().getDataConfirm() != null && !field.getProperties().getDataConfirm().isEmpty()) {
				sb.append(" data-confirm=\"" + field.getProperties().getDataConfirm() + "\"");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns CSS classes for given form field.
	 * @param field
	 */
	protected <T> String getInputClasses(FormField<T> field) {
		return styleRenderer.getInputClasses(field);
	}
	
	/**
	 * Returns placeholder attribute for the input of given form field.
	 * This attribute shows help (placeholder value) inside the form input
	 * before the user fills in his own value. 
	 * @param field
	 * @return
	 */
	protected <T> String getInputPlaceholderAttribute(FormField<T> field) {
		StringBuilder sb = new StringBuilder();
		if (field.getProperties().getPlaceholder() != null) {
			sb.append(" placeholder=\"" + escapeHtml(field.getProperties().getPlaceholder()) + "\"");
		}
		return sb.toString();
	}

	protected <T> String renderDatePickerScript(FormField<T> field) {
		return datePickerRenderer.renderDatePickerScript(field);
	}
	
	/**
	 * Renders client-side script for handling form field AJAX events.
	 * @param field
	 * @param inputMultiplicity whether given form field represents multiple form inputs
	 * @return
	 */
	protected <T> String renderFieldScript(FormField<T> field, InputMultiplicity inputMultiplicity) {
		return ajaxEventRenderer.renderFieldScript(field, inputMultiplicity);
	}

	protected <T> String renderTextFieldInternal(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupInput(field) + 
				renderMarkupMessageList(field)));
	}

	// --- Various field types - begin ---

	protected <T> String renderFieldSubmitButton(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupInputEnvelope(field, 
				renderMarkupButton(field)));
	}
	
	protected <T> String renderFieldLink(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupInputEnvelope(field, 
				renderMarkupLink(field)));
	}

	protected <T> String renderFieldHidden(FormField<T> field) {
		return renderMarkupInput(field) + newLine();
	}

	protected <T> String renderFieldText(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldColor(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldDate(FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldDateTime(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldDateTimeLocal(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTime(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldEmail(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldMonth(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldNumber(FormField<T> field) {
		// TODO: Support for min, max, step attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldRange(FormField<T> field) {
		// TODO: Support for min, max attributes
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldSearch(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTel(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldUrl(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldWeek(FormField<T> field) {
		return renderTextFieldInternal(field);
	}

	protected <T> String renderFieldTextArea(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupTextArea(field) + 
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldCheckbox(FormField<T> field) {
		return renderMarkupFormGroup(field,
			renderMarkupInputEnvelope(field,
				"<label>" +
				renderMarkupCheckbox(field) + 
				getLabelText(field) +
				"</label>" +
				renderMarkupMessageList(field))
		);
	}

	protected <T> String renderFieldPassword(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupInput(field) +
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldFileUpload(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupInput(field) + 
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldDatePicker(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupInput(field) + 
				renderDatePickerScript(field) + 
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldDropDownChoice(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupSelect(field) + 
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldMultipleCheckbox(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupChecks(field) + 
				renderMarkupMessageList(field)));
	}

	protected <T> String renderFieldRadioChoice(FormField<T> field) {
		return renderMarkupFormGroup(field, 
			renderMarkupFieldLabel(field) + 
			renderMarkupInputEnvelope(field, 
				renderMarkupChecks(field) + 
				renderMarkupMessageList(field)));
	}

	// --- /Various field types - end ---

	protected <T> String getLabelText(FormElement<T> element) {
		return labelRenderer.getLabelText(element);
	}
	
	protected <T> String getRequiredMark(FormElement<T> element) {
		return labelRenderer.getRequiredMark(element);
	}

	protected RenderContext getRenderContext() {
		return ctx;
	}
	
	String escapeHtml(String html) {
		return RenderUtils.escapeHtml(html);
	}
	
	String newLine() {
		return System.getProperty("line.separator");
	}
	
	String renderMarkupMessage(ConstraintViolationMessage msg) {
		return messageRenderer.renderMessage(msg);
	}
	
	private <T> boolean isCheckBox(FormField<T> field) {
		return Field.CHECK_BOX.getType().equals(field.getType());
	}
	
	private <T> String getChoiceTitle(ChoiceRenderer<T> choiceRenderer, T item, int itemIndex) {
		return escapeHtml(choiceRenderer.getItem(item, itemIndex).getTitle());
	}

	private <T> String getChoiceValue(ChoiceRenderer<T> choiceRenderer, T item, int itemIndex) {
		return escapeHtml(choiceRenderer.getItem(item, itemIndex).getId());
	}
	
	private <T> String getMaxSeverityClass(FormElement<T> mapping) {
		String maxSevClass = mapping.getMaxSeverityClass();
		if (maxSevClass != null && !maxSevClass.isEmpty()) {
			maxSevClass = "has-" + maxSevClass;
		}
		return maxSevClass;
	}
	
	private String renderMarkupOption(String value, String title, boolean selected) {
		StringBuilder sb = new StringBuilder();
		sb.append("<option value=\"" + escapeHtml(value) + "\"");
		if (selected) {
			sb.append(" selected=\"selected\"");
		}
		sb.append(">" + escapeHtml(title) + "</option>" + newLine());
		return sb.toString();
	}
	
	private <T> List<T> toSimplyTypedItems(List<? extends T> items) {
		List<T> retItems = new ArrayList<T>();
		if (items != null) {
			for (T item : items) {
				retItems.add(item);
			}
		}
		return retItems;
	}
}
