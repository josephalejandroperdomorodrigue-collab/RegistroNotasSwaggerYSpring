window.onload = function() {
  window.ui = SwaggerUIBundle({
    configUrl: "/v3/api-docs/swagger-config",
    dom_id: "#swagger-ui",
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout",
    operationsSorter: function(left, right) {
      const order = { post: 0, get: 1, put: 2, delete: 3 };
      const leftOrder = order[left.get("method")] ?? 99;
      const rightOrder = order[right.get("method")] ?? 99;
      return leftOrder - rightOrder
        || left.get("path").localeCompare(right.get("path"));
    }
  });
};
