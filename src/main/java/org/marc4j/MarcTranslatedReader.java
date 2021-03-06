
package org.marc4j;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.marc4j.converter.CharConverter;

import info.freelibrary.marc4j.converter.impl.AnselToUnicode;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.marc4j.util.Normalizer;

import java.util.List;

/**
 * @author Robert Haschart
 */
public class MarcTranslatedReader implements MarcReader {

    MarcReader reader;

    CharConverter convert;

    int unicodeNormalize = Normalizer.NONE;

    /**
     * Creates a MARC translated reader that can normalize Unicode.
     * 
     * @param r
     * @param unicodeNormalizeBool
     */
    public MarcTranslatedReader(MarcReader r, boolean unicodeNormalizeBool) {
        reader = r;
        convert = new AnselToUnicode();

        if (unicodeNormalizeBool) {
            this.unicodeNormalize = Normalizer.NFC;
        }
    }

    /**
     * Creates a MARC translated reader using the normalizer represented by the
     * supplied string.
     * 
     * @param r
     * @param unicodeNormalizeStr
     */
    public MarcTranslatedReader(MarcReader r, String unicodeNormalizeStr) {
        reader = r;
        convert = new AnselToUnicode();

        if (unicodeNormalizeStr.equals("KC")) {
            unicodeNormalize = Normalizer.NFKC;
        } else if (unicodeNormalizeStr.equals("KD")) {
            unicodeNormalize = Normalizer.NFKD;
        } else if (unicodeNormalizeStr.equals("C")) {
            unicodeNormalize = Normalizer.NFC;
        } else if (unicodeNormalizeStr.equals("D")) {
            unicodeNormalize = Normalizer.NFD;
        } else {
            unicodeNormalize = Normalizer.NONE;
        }
    }

    /**
     * Returns <code>true</code> if the reader has another {@link Record}.
     */
    public boolean hasNext() {
        return reader.hasNext();
    }

    /**
     * Returns the next {@link Record}.
     */
    public Record next() {
        Record rec = reader.next();
        Leader l = rec.getLeader();
        boolean is_utf_8 = false;

        if (l.getCharCodingScheme() == 'a') {
            is_utf_8 = true;
        }

        if (is_utf_8 && unicodeNormalize == Normalizer.NONE) {
            return (rec);
        }

        List<VariableField> fields = rec.getVariableFields();

        for (VariableField f : fields) {
            if (!(f instanceof DataField)) {
                continue;
            }

            DataField field = (DataField) f;
            List<Subfield> subfields = field.getSubfields();

            for (Subfield sf : subfields) {
                String oldData = sf.getData();
                String newData = oldData;

                if (!is_utf_8) {
                    newData = convert.convert(newData);
                }

                if (unicodeNormalize != Normalizer.NONE) {
                    newData = Normalizer.normalize(newData, unicodeNormalize);
                }

                if (!oldData.equals(newData)) {
                    sf.setData(newData);
                }
            }
        }

        l.setCharCodingScheme('a');
        rec.setLeader(l);

        return rec;
    }

}
