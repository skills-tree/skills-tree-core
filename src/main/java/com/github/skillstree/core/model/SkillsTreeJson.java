package com.github.skillstree.core.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillsTreeJson {

    private Chart chart;

    private NodeStructure nodeStructure;

    public Chart getChart() {
        return chart;
    }

    public SkillsTreeJson setChart(Chart chart) {
        this.chart = chart;
        return this;
    }

    public NodeStructure getNodeStructure() {
        return nodeStructure;
    }

    public SkillsTreeJson setNodeStructure(NodeStructure nodeStructure) {
        this.nodeStructure = nodeStructure;
        return this;
    }

    public static class Chart {

        private String container;

        private String rootOrientation;

        private boolean hideRootNode;

        private Connectors connectors;

        public String getContainer() {
            return container;
        }

        public Chart setContainer(String container) {
            this.container = container;
            return this;
        }

        public String getRootOrientation() {
            return rootOrientation;
        }

        public Chart setRootOrientation(String rootOrientation) {
            this.rootOrientation = rootOrientation;
            return this;
        }

        public boolean isHideRootNode() {
            return hideRootNode;
        }

        public Chart setHideRootNode(boolean hideRootNode) {
            this.hideRootNode = hideRootNode;
            return this;
        }

        public Connectors getConnectors() {
            return connectors;
        }

        public Chart setConnectors(Connectors connectors) {
            this.connectors = connectors;
            return this;
        }

        public static class Connectors {

            private String type;

            public String getType() {
                return type;
            }

            public Connectors setType(String type) {
                this.type = type;
                return this;
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NodeStructure {

        private Text text;

        private String innerHTML;

        @JsonProperty("HTMLclass")
        private String htmlClass;

        @JsonProperty("HTMLid")
        private String htmlId;

        private List<NodeStructure> children = new ArrayList<>();

        public Text getText() {
            return text;
        }

        public NodeStructure setText(Text text) {
            this.text = text;
            return this;
        }

        public String getInnerHTML() {
            return innerHTML;
        }

        public NodeStructure setInnerHTML(String innerHTML) {
            this.innerHTML = innerHTML;
            return this;
        }

        public String getHtmlClass() {
            return htmlClass;
        }

        public NodeStructure setHtmlClass(String htmlClass) {
            this.htmlClass = htmlClass;
            return this;
        }

        public String getHtmlId() {
            return htmlId;
        }

        public NodeStructure setHtmlId(String htmlId) {
            this.htmlId = htmlId;
            return this;
        }

        public List<NodeStructure> getChildren() {
            return children;
        }

        public NodeStructure setChildren(List<NodeStructure> children) {
            this.children = children;
            return this;
        }

        public NodeStructure addChild(NodeStructure child) {
            this.children.add(child);
            return this;
        }

        public static class Text {

            private String name;

            public String getName() {
                return name;
            }

            public Text setName(String name) {
                this.name = name;
                return this;
            }
        }
    }
}
