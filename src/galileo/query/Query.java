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

package galileo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates a Galileo query.
 *
 * @author malensek
 */
public class Query {

    private Map<String, List<Operation>> operations = new HashMap<>();

    public Query() { }

    public void addOperation(Operation op) {
        String operand = op.getOperandName();
        List<Operation> opList = operations.get(operand);
        if (opList == null) {
            List<Operation> newOpList = new ArrayList<>();
            newOpList.add(op);
            operations.put(operand, newOpList);
        } else {
            opList.add(op);
        }
    }

    public Set<String> getOperands() {
        return operations.keySet();
    }

    public List<Operation> getOperations(String operand) {
        return operations.get(operand);
    }

    @Override
    public String toString() {
        String str = "";
        for (List<Operation> opList : operations.values()) {
            for (Operation op : opList) {
                str += op + System.lineSeparator();
            }
        }

        return str;
    }
}
