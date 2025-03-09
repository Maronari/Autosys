package mirea.edu.autosys.utils;

import lombok.Getter;
import lombok.Setter;

public class NodeInfo {
    @Getter
    @Setter
    private String nodeId;
    @Getter
    @Setter
    private String nodeName;

    public NodeInfo(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }
}
