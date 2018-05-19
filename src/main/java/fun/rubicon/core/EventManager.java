package fun.rubicon.core;

import fun.rubicon.listener.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class EventManager {

    private final List<ListenerAdapter> listenerAdapters;

    public EventManager() {
        listenerAdapters = new ArrayList<>();
    }

    public void call(Event event) {
        for(ListenerAdapter adapter : listenerAdapters) {
            new Thread(() -> adapter.onEvent(event)).start();
        }
    }

    public void addListenerAdapter(ListenerAdapter listenerAdapter) {
        listenerAdapters.add(listenerAdapter);
    }

    public void addListenerAdapters(ListenerAdapter... listenerAdapters) {
        this.listenerAdapters.addAll(Arrays.asList(listenerAdapters));
    }

    public void removeListenerAdapter(ListenerAdapter listenerAdapter) {
        listenerAdapters.remove(listenerAdapter);
    }

    public List<ListenerAdapter> getListenerAdapters() {
        return listenerAdapters;
    }
}
