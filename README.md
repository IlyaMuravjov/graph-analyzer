# Graph analyzer

### Desktop network analyzing and visualization software

![demo.png](demo.png)

### General features
* Displays vertex and edge count
* Disabling/enabling vertex labels visibility
* Customizable edge color
* Configurable amount of rendered edges

### Layout
* Configurable maximum number of UI updates per second
* Displays iterations per second
* Supported layouts:
    * Random layout
    * ForceAtlas2 with the following settings:
        * Tolerance - adjusts speed-accuracy tradeoff
        * Scaling - adjusts resulting layout size
        * Strong gravity - makes gravity force attract distant vertices more
        * Gravity - adjusts force preventing disconnected components from drifting away
        * Prevent overlap - drastically repulses overlapping and nearly overlapping vertices
        * Dissuade hubs - grants vertices with a high indegree a more central position
        * Attraction types:
            * Linear - default setting
            * Logarithmic - makes the clusters tighter but converges slower in some cases
        * Edge weight exponent - adjusts edge weight influence
        * Multithreaded - performs some computations in parallel
        * Barnes-Hut approximation - considerably improves performance by approximating repulsion force
        * Barnes-Hut theta - specifies acceptable repulsion approximation

### Community detection
* Louvain method supporting viewing intermediate results

### Supported graph serialization formats
* SQLite
* Neo4j
