/**
 * Copyright 2015-2016 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package me.j360.trace.core.storage;

import java.util.concurrent.Executor;

import static me.j360.trace.core.storage.StorageAdapters.blockingToAsync;


/**
 * Test storage component that keeps all spans in memory, accepting them on the calling thread.
 */
public final class InMemoryStorage implements StorageComponent {
  final InMemorySpanStore spanStore = new InMemorySpanStore();
  final Executor callingThread = new Executor() {
    @Override public void execute(Runnable command) {
      command.run();
    }
  };
  final AsyncSpanStore asyncSpanStore = blockingToAsync(spanStore, callingThread);
  final AsyncSpanConsumer asyncConsumer = blockingToAsync(spanStore.spanConsumer, callingThread);

  @Override public InMemorySpanStore spanStore() {
    return spanStore;
  }

  @Override public AsyncSpanStore asyncSpanStore() {
    return asyncSpanStore;
  }

  public StorageAdapters.SpanConsumer spanConsumer() {
    return spanStore.spanConsumer;
  }

  @Override
  public AsyncSpanConsumer asyncSpanConsumer() {
    return asyncConsumer;
  }

  public void clear() {
    spanStore.clear();
  }

  public int acceptedSpanCount() {
    return spanStore.acceptedSpanCount;
  }

  @Override public CheckResult check() {
    return CheckResult.OK;
  }

  @Override public void close() {
  }
}
