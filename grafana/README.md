# Grafana Alerting Configuration for AKS Failure Detection

This directory contains Grafana alerting configuration files for monitoring and alerting on 5 critical AKS failure scenarios.

## Overview

The alerting system monitors the following failure scenarios:
- **CrashLoopBackOff**: Pods stuck in crash loop
- **OOMKilled**: Containers terminated due to out of memory
- **ImagePullBackOff**: Pods unable to pull container images
- **Pending Pods**: Pods stuck in pending state
- **High Restart Rate**: Pods with frequent restarts (liveness probe failures)

## Files

- `failure-detection-dashboard.json` - Grafana dashboard with panels for all 5 failure scenarios
- `alerts/failure-alert-rules.yaml` - Alert rule definitions for all 5 scenarios
- `alerts/contact-points.yaml` - Email contact point configuration
- `alerts/notification-policies.yaml` - Alert routing and grouping policies

## Prerequisites

1. **Azure Managed Grafana** instance
2. **Azure Monitor workspace** with Prometheus metrics enabled
3. **AKS cluster** with monitoring enabled
4. **kube-state-metrics** deployed in your AKS cluster

## Setup Instructions

### Step 1: Import the Dashboard

1. Navigate to your Azure Managed Grafana instance
2. Go to **Dashboards** → **Import**
3. Click **Upload JSON file** and select `failure-detection-dashboard.json`
4. Select your Prometheus data source when prompted
5. Click **Import**

### Step 2: Configure Email Contact Point

Before applying the alert configurations, you need to configure email settings:

#### Option A: Using Azure Managed Grafana (Recommended)

Azure Managed Grafana comes with SMTP pre-configured. You only need to update the email address:

1. Navigate to **Alerting** → **Contact points**
2. Edit the `contact-points.yaml` file and replace `<your-email@example.com>` with your actual email address:
   ```yaml
   settings:
     addresses: "your-email@example.com"
   ```
3. Upload the modified file via the Grafana UI:
   - Go to **Alerting** → **Contact points** → **Add contact point**
   - Name: `AKS-Failure-Email-Alerts`
   - Integration: **Email**
   - Addresses: Enter your email address
   - Click **Test** to verify
   - Click **Save contact point**

#### Option B: Manual Configuration via UI

1. In Azure Managed Grafana, navigate to **Alerting** → **Contact points**
2. Click **Add contact point**
3. Configure:
   - **Name**: `AKS-Failure-Email-Alerts`
   - **Integration**: Email
   - **Addresses**: Enter your email address (comma-separated for multiple)
   - **Single email**: Unchecked (to send individual emails per alert)
   - **Disable resolved message**: Unchecked (to receive resolution notifications)
4. Click **Test** to send a test email
5. Click **Save contact point**

#### Option C: Custom SMTP Configuration (If needed)

If you need custom SMTP settings:

1. Navigate to **Configuration** → **Settings**
2. Under **SMTP**, configure:
   ```
   Host: smtp.office365.com:587 (for Office 365)
   User: your-email@example.com
   Password: your-app-password
   From address: your-email@example.com
   From name: AKS Monitoring
   ```
3. Save and restart Grafana (if required)

### Step 3: Apply Alert Rules

#### Via Grafana UI (Recommended for Azure Managed Grafana)

1. Navigate to **Alerting** → **Alert rules**
2. Click **New alert rule**
3. For each alert in `failure-alert-rules.yaml`, create a rule:

   **CrashLoopBackOff Alert:**
   - Name: `Pod CrashLoopBackOff Detected`
   - Query: `count(kube_pod_container_status_waiting_reason{reason="CrashLoopBackOff"} == 1) or vector(0)`
   - Condition: `> 0`
   - For: `5m`
   - Folder: `AKS Monitoring`
   - Labels: `severity=critical, scenario=crashloop`

   **OOMKilled Alert:**
   - Name: `Container OOMKilled Detected`
   - Query: `count(kube_pod_container_status_last_terminated_reason{reason="OOMKilled"} == 1) or vector(0)`
   - Condition: `> 0`
   - For: `1m`
   - Labels: `severity=critical, scenario=oom`

   **ImagePullBackOff Alert:**
   - Name: `ImagePullBackOff Detected`
   - Query: `count(kube_pod_container_status_waiting_reason{reason="ImagePullBackOff"} == 1) or vector(0)`
   - Condition: `> 0`
   - For: `5m`
   - Labels: `severity=warning, scenario=imagepull`

   **Pending Pod Alert:**
   - Name: `Pod Pending Too Long`
   - Query: `count(kube_pod_status_phase{phase="Pending"} == 1) or vector(0)`
   - Condition: `> 0`
   - For: `10m`
   - Labels: `severity=warning, scenario=pending`

   **High Restart Rate Alert:**
   - Name: `High Pod Restart Rate (Liveness Failure)`
   - Query: `count(rate(kube_pod_container_status_restarts_total[5m]) > 0.5) or vector(0)`
   - Condition: `> 0`
   - For: `5m`
   - Labels: `severity=critical, scenario=liveness`

4. Click **Save and exit** for each rule

#### Via Provisioning API (Advanced)

If using Grafana provisioning or infrastructure as code:

1. Upload `failure-alert-rules.yaml` to your provisioning directory
2. Ensure the file is in `/etc/grafana/provisioning/alerting/`
3. Restart Grafana or reload provisioning

### Step 4: Configure Notification Policies

1. Navigate to **Alerting** → **Notification policies**
2. Click **New nested policy** under the default policy
3. Configure:
   - **Matching labels**: `scenario =~ "crashloop|oom|imagepull|pending|liveness"`
   - **Contact point**: `AKS-Failure-Email-Alerts`
   - **Group by**: `grafana_folder`, `alertname`
   - **Timing options**:
     - Group wait: `30s`
     - Group interval: `5m`
     - Repeat interval: `4h`
4. Click **Save policy**

### Step 5: Verify Configuration

1. **Test alerts manually**:
   - Go to **Alerting** → **Alert rules**
   - Click on any rule and select **Test**
   - Verify the query returns expected results

2. **Check contact point**:
   - Go to **Alerting** → **Contact points**
   - Select `AKS-Failure-Email-Alerts`
   - Click **Test** to send a test email
   - Verify you receive the email

3. **Trigger a test alert** (optional):
   - Deploy a pod with an invalid image to trigger ImagePullBackOff
   - Wait for the alert to fire
   - Verify email notification is received

## Alert Thresholds

| Alert | Condition | Duration | Severity |
|-------|-----------|----------|----------|
| CrashLoopBackOff | count > 0 | 5 minutes | Critical |
| OOMKilled | count > 0 | 1 minute | Critical |
| ImagePullBackOff | count > 0 | 5 minutes | Warning |
| Pending Pod | count > 0 | 10 minutes | Warning |
| High Restart Rate | rate > 0.5 restarts/sec (over 5m window) | 5 minutes | Critical |

## Email Notification Content

Each alert email includes:
- **Alert Name**: The title of the alert
- **Severity**: Critical or Warning
- **Summary**: Brief description of the issue
- **Description**: Detailed information about the failure
- **Labels**: scenario, severity
- **Time**: When the alert was triggered
- **Link**: Direct link to the Grafana dashboard

## Troubleshooting

### Alerts Not Firing

1. **Verify Prometheus data source**:
   - Ensure Prometheus is connected and returning metrics
   - Check queries in Grafana Explore: `kube_pod_container_status_waiting_reason`

2. **Check alert rule state**:
   - Navigate to **Alerting** → **Alert rules**
   - Verify the rule state is not "NoData" or "Error"

3. **Review evaluation**:
   - Click on the alert rule
   - Check the **State history** tab
   - Look for evaluation errors

### Emails Not Received

1. **Verify contact point configuration**:
   - Test the contact point manually
   - Check spam/junk folders
   - Verify email address is correct

2. **Check SMTP settings** (if using custom SMTP):
   - Review Grafana logs: `kubectl logs -n grafana <grafana-pod>`
   - Verify SMTP credentials and server

3. **Review notification policies**:
   - Ensure matchers are correct
   - Verify the alert labels match the policy

### No Data in Dashboard

1. **Verify kube-state-metrics**:
   ```bash
   kubectl get pods -n kube-system | grep kube-state-metrics
   ```

2. **Check Prometheus scraping**:
   - Query Prometheus directly for: `kube_pod_container_status_waiting_reason`
   - Verify metrics are being scraped

3. **Validate data source**:
   - In Grafana, go to **Connections** → **Data sources**
   - Select your Prometheus instance
   - Click **Test** to verify connection

## Azure Managed Grafana Specific Notes

1. **Authentication**: Azure Managed Grafana uses Azure AD authentication by default
2. **SMTP**: Pre-configured, no need to set up SMTP manually
3. **Permissions**: Ensure you have "Grafana Admin" role to configure alerts
4. **Provisioning**: File-based provisioning may have limitations; use UI for Azure Managed Grafana
5. **Data sources**: Azure Monitor workspace integration is automatic when properly configured

## Advanced Configuration

### Customizing Alert Messages

Edit the `annotations` section in `failure-alert-rules.yaml`:

```yaml
annotations:
  summary: "Custom summary message"
  description: "Custom detailed description with {{$labels.namespace}}/{{$labels.pod}}"
  runbook_url: "https://wiki.example.com/runbooks/aks-failures"
```

### Adding More Contact Points

You can add Slack, Microsoft Teams, or PagerDuty:

1. Navigate to **Alerting** → **Contact points**
2. Click **Add contact point**
3. Select your integration type
4. Configure according to the integration

### Silencing Alerts

To temporarily silence specific alerts:

1. Navigate to **Alerting** → **Silences**
2. Click **Add silence**
3. Configure matchers (e.g., `scenario=imagepull`)
4. Set duration
5. Click **Submit**

## Monitoring Multiple AKS Clusters

To monitor multiple clusters:

1. Configure a Prometheus data source for each cluster
2. Duplicate the dashboard for each cluster
3. Update the data source in each dashboard panel
4. Add cluster name as a template variable

## Support and Resources

- [Azure Managed Grafana Documentation](https://learn.microsoft.com/en-us/azure/managed-grafana/)
- [Grafana Alerting Documentation](https://grafana.com/docs/grafana/latest/alerting/)
- [Prometheus Query Examples](https://prometheus.io/docs/prometheus/latest/querying/examples/)
- [kube-state-metrics Metrics](https://github.com/kubernetes/kube-state-metrics/tree/main/docs)

## Contributing

To modify alert thresholds or add new alerts:

1. Edit the YAML files in the `alerts/` directory
2. Test the changes in a development Grafana instance
3. Update this README with any configuration changes
4. Submit a pull request

## License

This configuration is part of the Azure SRE Agent Demo repository and follows the same MIT license.
