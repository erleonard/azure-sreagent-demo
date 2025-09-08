<h1 align="center">
  <img src="./docs/logo.jpg" alt="logo" width="128" />
  <br>
  Azure SRE Agent Demo
  <br>
</h1>

Azure SRE Agent Demo

### App Deployment to Azure Kubernetes Service (AKS)

Build image with Azure Container Registry (ACR)
```bash
# Variables - Update these with your values
ACR_NAME="your-acr-name"
IMAGE_NAME="sreagent-memory-demo"
IMAGE_TAG="latest"

# Build and push the image to ACR
az acr build --registry $ACR_NAME --image $IMAGE_NAME:$IMAGE_TAG .
```

Update the image name in k8s-deployment.yaml first
```bash
kubectl apply -f k8s-deployment.yaml
```
validate the deployment
```bash
kubectl get pods -l app=sreagent-memory-demo
kubectl get services
```

## :wave: Contributors
- [Eric Leonard](https://github.com/erleonard)
- [Azure-Samples](https://github.com/Azure-Samples/app-service-dotnet-agent-tutorial)

## :warning:  License

This repository is licensed under the MIT license. See the [LICENSE](LICENSE) file for more information.