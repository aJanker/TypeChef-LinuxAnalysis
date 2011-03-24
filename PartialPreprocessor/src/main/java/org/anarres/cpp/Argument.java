/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.anarres.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A macro argument.
 *
 * This encapsulates a raw and preprocessed token stream.
 */
@SuppressWarnings("serial")
/* pp */ class Argument extends ArrayList<Token> {
	public static final int	NO_ARGS = -1;
	private boolean omittedArg;

	private List<Token>	expansion;

	public Argument() {
		this.expansion = null;
	}

	public void addToken(Token tok) {
	    	if (!omittedArg) {
			add(tok);
		} else {
			throw new IllegalArgumentException("Tried to add a token to omittedVariadicArgument.");
		}
	}

	public static Argument omittedVariadicArgument() {
	        Argument a = new Argument();
	        a.omittedArg = true;
                a.expansion = Collections.emptyList();
	        return a;
	}

	public boolean isOmittedArg() {
		return omittedArg;
        }

	/* pp */ void expand(Preprocessor p, boolean inlineCppExpression, String macroName)
						throws IOException,
								LexerException {
		/* Cache expansion. */
		if (expansion == null) {
			this.expansion = p.macro_expandArgument(this, inlineCppExpression, macroName);
			// System.out.println("Expanded arg " + this);
		}
	}

	public Iterator<Token> expansion() {
		return expansion.iterator();
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Argument(");
		// buf.append(super.toString());
		buf.append("raw=[ ");
		for (int i = 0; i < size(); i++)
			buf.append(get(i).getText());
		buf.append(" ];expansion=[ ");
		if (expansion == null)
			buf.append("null");
		else
			for (int i = 0; i < expansion.size(); i++)
				buf.append(expansion.get(i).getText());
		buf.append(" ])");
		return buf.toString();
	}

}
