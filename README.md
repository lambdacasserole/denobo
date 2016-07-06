# Denobo
Multi-agent middleware for the Java platform.

The Denobo project was cooked up in around a month as a second-year university project exploring multi-agent middleware. Its name 'Denobo' is a neologism derived from thephrase 'Definitely not Boris' to emphasise its radically different approach to threaded agentware from the software that served as the inspiration for the official Denobo Java library, a piece of agentware named [Boris](https://www.scm.tees.ac.uk/isg/website/index.php?page=downloads_boris).

This difference lies in the structure of the middleware itself. While other multi-agent middleware libraries emphasise hierarchy and parent-child relationships between agents, Denobo maintains a completely flat network structure and strictly peer-to-peer approach that offers extra redundancy, flexibility and modularity.

This is not for one moment to say that this approach is superior to a more structured hierarchical approach, only that it offers something different, and allows an end-user to use an extremely thin API to set up and use Denobo networks from their own Java software. For instance, on a Denobo network the only way to have an independent subsystem is to have it isolated and not connected to anything else. As soon as an agent is linked in to a Denobo network, it and everything it is connected to is discoverable, and can both send and receive traffic via the new link. 
