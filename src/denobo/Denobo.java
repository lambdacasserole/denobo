package denobo;

/**
 *
 * @author Saul Johnson
 */
public class Denobo implements RoutingWorkerListener {
 
    public void run() {
        
        Agent a = new Agent("A");
        
        Agent b = new Agent("B");
        a.connectActor(b);
        
        Agent c = new Agent("C");
        b.connectActor(c);
        
        Agent d = new Agent("D");
        c.connectActor(d);
        
        Agent e = new Agent("E");
        d.connectActor(e);
        
        Agent f = new Agent("F");
        e.connectActor(f);
        
        
        RoutingWorker w = new RoutingWorker(a, "F");
        w.addRoutingWorkerListener(this);
        w.mapRouteAsync();
        
    }
    
    public static void main(String[] args) {
        
     Denobo p = new Denobo();
     p.run();
        
    }

    @Override
    public void routeCalculated(String destinationAgentName, RoutingQueue routeW) {
        
        System.out.println(routeW.toString());
        
    }
    
}
