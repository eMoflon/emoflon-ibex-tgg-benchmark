/*
 * Copyright 2015 Terracotta, Inc., a Software AG company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.ipceventbus.event;

/**
 * @author Mathieu Carbou
 */
public interface EventBus extends EventSender {

  /**
   * Register a new listener for an event
   *
   * @param event    The event name
   * @param listener The listener to register
   */
  void on(String event, EventListener listener);

  /**
   * Register a new listener for all event
   *
   * @param listener The listener to register
   */
  void on(EventListener listener);

  /**
   * Unbind all listeners of an event
   *
   * @param event the event name
   */
  void unbind(String event);

  /**
   * unbind this listener from all events
   *
   * @param listener the listener
   */
  void unbind(EventListener listener);

  /**
   * unbind a listener from a specific event
   *
   * @param event    the event name
   * @param listener the listener
   */
  void unbind(String event, EventListener listener);

  final class Builder extends BaseBuilder<Builder> {
  }
}
