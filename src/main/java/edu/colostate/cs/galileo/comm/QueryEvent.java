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

package edu.colostate.cs.galileo.comm;

import java.io.IOException;

import edu.colostate.cs.galileo.event.Event;
import edu.colostate.cs.galileo.query.Query;
import edu.colostate.cs.galileo.serialization.SerializationException;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;

/**
 * Contains an internal query between StorageNodes.
 *
 * @author malensek
 */
public class QueryEvent implements Event {
  private String id;
  private Query query;

  public QueryEvent(String id, Query query) {
    this.id = id;
    this.query = query;
  }

  @Deserialize
  public QueryEvent(SerializationInputStream in)
      throws IOException, SerializationException {
    id = in.readString();
    query = new Query(in);
  }

  public Query getQuery() {
    return query;
  }

  public String getQueryId() {
    return id;
  }

  @Override
  public void serialize(SerializationOutputStream out)
      throws IOException {
    out.writeString(id);
    out.writeSerializable(query);
  }
}
