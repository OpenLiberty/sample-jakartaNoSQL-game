package org.jakartaee.sample.server.event;

public sealed interface GameServerEvent
        permits OnSessionOpenEvent, OnSessionMessageEvent, OnSessionCloseEvent, OnSessionErrorEvent {

}
