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
package de.mhus.lib.core.jmx;

import de.mhus.lib.annotations.jmx.JmxManaged;

@JmxManaged(descrition = "Float Value")
public class JmxFloat extends MJmx {

    private float value;

    public JmxFloat(String name) {
        super(true, name);
    }

    public JmxFloat setValue(float value) {
        this.value = value;
        return this;
    }

    @JmxManaged
    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
