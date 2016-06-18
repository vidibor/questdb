/*******************************************************************************
 *    ___                  _   ____  ____
 *   / _ \ _   _  ___  ___| |_|  _ \| __ )
 *  | | | | | | |/ _ \/ __| __| | | |  _ \
 *  | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *   \__\_\\__,_|\___||___/\__|____/|____/
 *
 * Copyright (C) 2014-2016 Appsicle
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.questdb.ql.impl.map;

import com.questdb.factory.JournalReaderFactory;
import com.questdb.ql.RecordCursor;
import com.questdb.ql.StorageFacade;
import com.questdb.std.ObjList;
import com.questdb.store.SymbolTable;

public class DirectMapStorageFacade implements StorageFacade {
    private final int split;
    private final int keyCount;
    private ObjList<SymbolTable> symbolTables;
    private JournalReaderFactory factory;

    public DirectMapStorageFacade(int split, int keyCount) {
        this.split = split;
        this.keyCount = keyCount;
    }

    @Override
    public JournalReaderFactory getFactory() {
        return factory;
    }

    @Override
    public SymbolTable getSymbolTable(int index) {
        return symbolTables.getQuick(index);
    }

    @Override
    public SymbolTable getSymbolTable(String name) {
        return null;
    }

    public void prepare(RecordCursor cursor) {
        StorageFacade parent = cursor.getStorageFacade();
        this.factory = parent.getFactory();

        for (int i = 0; i < keyCount; i++) {
            SymbolTable tab = parent.getSymbolTable(i);
            if (tab != null) {
                if (symbolTables == null) {
                    symbolTables = new ObjList<>();
                }
                symbolTables.extendAndSet(split + i, tab);
            }
        }
    }
}