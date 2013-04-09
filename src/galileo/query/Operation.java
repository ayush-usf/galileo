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
import java.util.List;

/**
 * Encapsulates a query operation.  This involves an operand, and multiple
 * {@link Expression} pairs.  Each expression pair is joined with the logical
 * AND operator.
 *
 * @author malensek
 */
public class Operation {

    private String operandName;
    private List<Expression> expressions = new ArrayList<>();

    public Operation(String operandName, Expression expression) {
        this.operandName = operandName;
        addExpressions(expression);
    }

    public Operation(String operandName, Expression... expressions) {
        this.operandName = operandName;
        addExpressions(expressions);
    }

    public void addExpressions(Expression... expressions) {
        for (Expression expression : expressions) {
            this.expressions.add(expression);
        }
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public String getOperandName() {
        return operandName;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < expressions.size(); ++i) {
            str += operandName + " " + expressions.get(i);

            if (i < expressions.size() - 1) {
                str += " && ";
            }
        }

        return "(" + str + ")";
    }
}
