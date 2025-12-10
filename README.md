<h1 align="center">
  <img src="./docs/logo.jpg" alt="logo" width="128" />
  <br>
  Azure SRE Agent Demo
  <br>
</h1>

## Prerequisites
- [Azure Subscription](https://azure.microsoft.com/free/)
- Preview Access: [Azure SRE Agent application](https://go.microsoft.com/fwlink/?linkid=2319540)

## :rocket: Getting Started
- Create SRE Agent: [Create and Use an agent in Azure SRE Agent Preview](https://learn.microsoft.com/en-us/azure/sre-agent/usage)

## :chart_with_upwards_trend: Monitoring & Dashboards

### Grafana Dashboard for Failure Detection

This repository includes a comprehensive Grafana dashboard for monitoring Kubernetes failure scenarios in AKS clusters. The dashboard visualizes:

- **CrashLoopBackOff** - Pods stuck in crash loops
- **OOMKilled** - Containers terminated due to out-of-memory errors
- **ImagePullBackOff** - Pods unable to pull container images
- **Pending Pods** - Pods stuck in pending state due to scheduling issues
- **Liveness Probe Failures** - Pods with high restart rates
- **Summary Statistics** - Overview of all failure types

### Importing the Dashboard into Azure Managed Grafana

1. **Navigate to your Azure Managed Grafana instance**:
   ```bash
   # Get your Grafana URL
   az grafana show --name <grafana-name> --resource-group <resource-group> --query properties.endpoint -o tsv
   ```

2. **Access the Grafana UI**:
   - Open the Grafana URL in your browser
   - Authenticate using your Azure credentials

3. **Import the Dashboard**:
   - Click on **"+"** (Create) in the left sidebar
   - Select **"Import"**
   - Click **"Upload JSON file"** or paste the contents of `grafana/failure-detection-dashboard.json`
   - Select your **Azure Monitor managed service for Prometheus** as the data source
   - Click **"Import"**

4. **Configure the Data Source**:
   - Ensure you have Azure Monitor managed service for Prometheus configured in your Grafana instance
   - The dashboard will automatically use the selected Prometheus data source
   - You can filter by namespace using the dropdown at the top of the dashboard

5. **Prerequisites**:
   - Azure Managed Grafana instance
   - Azure Monitor managed service for Prometheus with kube-state-metrics enabled
   - AKS cluster connected to Azure Monitor

### Dashboard Features

- **Auto-refresh**: 30-second refresh interval for real-time monitoring
- **Namespace filtering**: Filter view by specific namespaces
- **Color-coded alerts**: Red for critical failures, orange for warnings
- **Links to documentation**: Each panel includes links to relevant Kubernetes documentation
- **Collapsible rows**: Organized layout with collapsible sections
- **Time range selection**: Default 1-hour view with customizable range

For more information on Azure Managed Grafana, see the [official documentation](https://learn.microsoft.com/en-us/azure/managed-grafana/).

## :wave: Contributors
- [Eric Leonard](https://github.com/erleonard)

## :warning:  License

This repository is licensed under the MIT license. See the [LICENSE](LICENSE) file for more information.