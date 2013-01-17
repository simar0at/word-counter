/*
 * Copyright (c) 2012, Omar Siam. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  I designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package net.homeunix.siam.wordcounter;

public class TokenWithContext {
	public String word;
	public String[] context;
	
	TokenWithContext(String word, CircularBuffer<TokenAndType> context) {
		this.word = word;
		this.context = new String[Run.CONTEXT_LENGTH];
		for (int i = 0; i < Run.CONTEXT_LENGTH; i++)
			this.context[i] = context.get(i).token;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s: context)
			sb.append(s);
		return sb.toString();
	}
}