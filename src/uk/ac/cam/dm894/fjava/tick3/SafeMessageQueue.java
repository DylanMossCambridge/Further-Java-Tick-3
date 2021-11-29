/*
 * Copyright 2021 Andrew Rice <acr31@cam.ac.uk>, Alastair Beresford <arb33@cam.ac.uk>, Dylan Moss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.dm894.fjava.tick3;

public class SafeMessageQueue<T> implements MessageQueue<T> {
  private static class Link<L> {
    L val;
    Link<L> next;

    Link(L val) {
      this.val = val;
      this.next = null;
    }
  }

  private Link<T> first = null;
  private Link<T> last = null;

  public synchronized void put(T val) {
    Link<T> newLink = new Link<>(val);

    if(last != null) {
      last.next = newLink;
    }

    if(first == null) {
      first = newLink;
    }

    last = newLink;

    this.notify();
  }

  public synchronized T pop() {
    if (first != null) {
      T val = first.val;
      if (first == last) {
        first = null;
        last = null;
      } else {
        first = first.next;
      }
      return val;
    } else {
      return null;
    }
  }

  public synchronized T take() {
    while (first == null) { // use a loop to block thread until data is available
      try {
        this.wait();
      } catch (InterruptedException ie) {
        // Ignored exception
      }
    }
    return pop();
  }
}
