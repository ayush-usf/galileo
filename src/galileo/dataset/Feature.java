/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package galileo.dataset;

public class Feature implements Comparable<Feature> {

    protected String name;
    protected FeatureType type = FeatureType.FLOAT;
    protected String description;
    protected double value;

    public Feature(String name) {
        this.name = name;
    }

    public Feature(double value) {
        this.value = value;
    }

    public Feature(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public FeatureType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Feature f) {
        Double d1 = new Double(f.getValue());
        Double d2 = new Double(this.getValue());

        return d2.compareTo(d1);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
