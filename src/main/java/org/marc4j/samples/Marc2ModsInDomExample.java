/**
 * Copyright (C) 2002-2006 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.marc4j.samples;

import info.freelibrary.marc4j.converter.impl.AnselToUnicode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.w3c.dom.Document;

/**
 * Writes MARC XML to a DOM document.
 *
 * @author Bas Peters
 */
public class Marc2ModsInDomExample {

    /**
     * The main class for the Marc2ModsInDomExample.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String args[]) throws Exception {
        final InputStream input = new FileInputStream("src/test/resources/summerland.mrc");
        final MarcReader reader = new MarcStreamReader(input);
        final Source stylesheet = new StreamSource(new File("src/test/resources/MARC21slim2MODS3.xsl"));

        final DOMResult result = new DOMResult();
        final MarcXmlWriter writer = new MarcXmlWriter(result, stylesheet);

        writer.setConverter(new AnselToUnicode());

        while (reader.hasNext()) {
            final Record record = reader.next();
            writer.write(record);
        }

        writer.close();

        final Document doc = (Document) result.getNode();

        System.out.println(doc.getDocumentElement().getLocalName());

    }
}
