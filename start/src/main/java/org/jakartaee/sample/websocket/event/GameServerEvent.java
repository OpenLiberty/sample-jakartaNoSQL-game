package org.jakartaee.sample.websocket.event;

public sealed interface GameServerEvent
        permits OnSessionOpenEvent, OnSessionMessageEvent, OnSessionCloseEvent, OnSessionErrorEvent {

}
