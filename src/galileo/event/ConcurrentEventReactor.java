/*
Copyright (c) 2014, Colorado State University
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

package galileo.event;

/**
 * Extends the single-threaded reactor implementation defined by
 * {@link EventReactor} to enable multiple worker threads for processing events
 * concurrently.  This assumes that the object with event handlers can deal
 * with being accessed by multiple threads concurrently.
 * <p>
 * In all likelihood, spinning up threads when necessary using the
 * single-threaded EventReactor would be an easier and fairly performant
 * alternative to using this class.
 *
 * @author malensek
 */
public class ConcurrentEventReactor extends EventReactor {

    private int poolSize;

    /**
     * @param handlerObject an Object instance that contains the implementations
     * for event handlers, denoted by the {@link EventReactor} annotation.
     * @param eventMap a EventMap implementation that provides a mapping from
     * integer identification numbers to specific classes that represent an
     * event.
     * @param poolSize the number of worker threads this concurrent event mapper
     * should maintain.
     */
    public ConcurrentEventReactor(
            Object handlerObject, EventMap eventMap, int poolSize) {
        super(handlerObject, eventMap);
        this.poolSize = poolSize;
    }
}
