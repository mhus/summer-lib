/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.operation;

import java.util.HashMap;
import java.util.Map;

public class Successful extends OperationResult {

    public Successful(Operation operation) {
        this(operation, "", 0, (String) null);
    }

    public Successful(Operation operation, String msg) {
        this(operation, msg, 0, (String) null);
    }

    public Successful(Operation operation, String msg, int rc) {
        this(operation, msg, rc, (String) null);
    }

    public Successful(Operation operation, String msg, Map<?, ?> result) {
        this(operation, msg, 0, result);
    }

    @SuppressWarnings("deprecation")
    public Successful(Operation operation, String msg, int rc, Map<?, ?> result) {
        setOperationPath(operation.getDescription().getPath());
        setCaption(operation.getDescription().getCaption());
        setMsg(msg);
        setResult(result);
        setReturnCode(rc);
        setSuccessful(true);
    }

    @SuppressWarnings("deprecation")
    public Successful(String path, String msg, int rc, Map<?, ?> result) {
        setOperationPath(path);
        setCaption("");
        setMsg(msg);
        setResult(result);
        setReturnCode(rc);
        setSuccessful(true);
    }

    public Successful(Operation operation, String msg, String result) {
        this(operation, msg, 0, result);
    }

    @SuppressWarnings("deprecation")
    public Successful(Operation operation, String msg, int rc, String result) {
        setOperationPath(operation.getDescription().getPath());
        setCaption(operation.getDescription().getCaption());
        setMsg(msg);
        setResult(result);
        setReturnCode(rc);
        setSuccessful(true);
    }

    @SuppressWarnings("deprecation")
    public Successful(String path, String msg, int rc, String result) {
        setOperationPath(path);
        setCaption("");
        setMsg(msg);
        setResult(result);
        setReturnCode(rc);
        setSuccessful(true);
    }

    public Successful(Operation operation, String msg, String... keyValues) {
        this(operation.getDescription().getPath(), msg, 0, keyValues);
    }

    public Successful(String path) {
        this(path, "ok", 0);
    }

    @SuppressWarnings("deprecation")
    public Successful(String path, String msg, int rc, String... keyValues) {
        setOperationPath(path);
        setCaption("");
        setMsg(msg);
        setReturnCode(rc);
        setSuccessful(true);
        HashMap<Object, Object> r = new HashMap<>();
        if (keyValues != null) {
            for (int i = 0; i < keyValues.length - 1; i += 2)
                if (keyValues.length > i + 1) r.put(keyValues[i], keyValues[i + 1]);
            setResult(r);
        }
    }
}
