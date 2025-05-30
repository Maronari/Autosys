package mirea.edu.autosys.service;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.beans.factory.annotation.Autowired;

public class PasterizationService {
    @Autowired
    private OpcUaService opcUaService;
    @Autowired
    private DatabaseService databaseService;

    public void processNodeValue(String endpointUrl, NodeId nodeId, Object value) {
        String nodeIdentifier = nodeId.getIdentifier().toString();
        double doubleValue = Double.parseDouble(value.toString());

        String endpoint = databaseService.getSensorByParamName(nodeIdentifier).getSensorOpcuaEndpoint();

        switch(nodeIdentifier) {
            case "P1":
                if (doubleValue == 0)
                {
                    
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                    
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                break;
            case "Exp1":
                if (doubleValue < 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "P2":
                if (doubleValue < 3000000)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "L1":
                if (doubleValue == 1)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "T2":
                if (doubleValue > 82)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else if (doubleValue < 70)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "Exp2":
                if (doubleValue < 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "P3":
                if (doubleValue < 200000)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "T3":
                if (doubleValue > 70)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else if (doubleValue < 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "T4":
                if (doubleValue == 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else if (doubleValue < 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
            case "Exp3":
                if (doubleValue < 4)
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "1");
                }
                else
                {
                    opcUaService.writeValue(endpoint, nodeId.toParseableString(), "0");
                }
                break;
        }
    }
}
