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
package net.formio.ajax.action;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import net.formio.AbstractRequestParams;
import net.formio.ajax.AjaxParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.JsEvent;
import net.formio.render.FormRenderer;
import net.formio.render.RenderContext;
import net.formio.render.tdi.TdiResponseBuilder;

import org.junit.Test;

public class JsEventToActionTest {

	@Test
	public void testGetUrl() {
		JsEvent jsEvent = JsEvent.BLUR;
		JsEventToAction<Long> action = new JsEventToAction<Long>(jsEvent, new AjaxAction<Long>() {

			@Override
			public AjaxResponse<Long> apply(AbstractRequestParams requestParams) {
				TdiResponseBuilder rb = new TdiResponseBuilder(new FormRenderer(new RenderContext(Locale.ENGLISH)));
				return new AjaxResponse<Long>(rb.status("OK").asString());
			}
		});
		String urlBase = "http://localhost:8080/myform";
		String url = action.getUrl(urlBase, null);
		assertEquals(urlBase + "?" + AjaxParams.EVENT + "=" + jsEvent.getEventName(), url);
	}

}
