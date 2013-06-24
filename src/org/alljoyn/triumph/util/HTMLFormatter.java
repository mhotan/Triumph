/******************************************************************************
 * Copyright 2013, Qualcomm Innovation Center, Inc.
 *
 *    All rights reserved.
 *    This file is licensed under the 3-clause BSD license in the NOTICE.txt
 *    file for this project. A copy of the 3-clause BSD license is found at:
 *
 *        http://opensource.org/licenses/BSD-3-Clause.
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the license is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the license for the specific language governing permissions and
 *    limitations under the license.
 ******************************************************************************/

package org.alljoyn.triumph.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.sun.webpane.webkit.network.data.Handler;

/**
 * 
 * @author Michael 
 */
public class HTMLFormatter extends Formatter {

	/**
	 * Formatter for date input
	 */
	private final DateFormat mFormat;

	public HTMLFormatter() {
		mFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm"); 
	}
	
	// This method is called for every log records
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		// Bold any levels >= WARNING
		buf.append("<tr>");
		buf.append("<td>");

		if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
			buf.append("<b>");
			buf.append(rec.getLevel());
			buf.append("</b>");
		} else {
			buf.append(rec.getLevel());
		}
		buf.append("</td>");
		buf.append("<td>");
		buf.append(calcDate(rec.getMillis()));
		buf.append(' ');
		buf.append(formatMessage(rec));
		buf.append('\n');
		buf.append("<td>");
		buf.append("</tr>\n");
		return buf.toString();
	}

	private String calcDate(long millisecs) {
		Date resultdate = new Date(millisecs);
		return mFormat.format(resultdate);
	}

	// This method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h) {
		return "<HTML>\n<HEAD>\n" + (new Date()) 
				+ "\n</HEAD>\n<BODY>\n<PRE>\n"
				+ "<table width=\"100%\" border>\n  "
				+ "<tr><th>Level</th>" +
				"<th>Time</th>" +
				"<th>Log Message</th>" +
				"</tr>\n";
	}

	// This method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h) {
		return "</table>\n  </PRE></BODY>\n</HTML>\n";
	}

}
