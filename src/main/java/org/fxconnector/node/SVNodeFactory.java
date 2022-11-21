/*
 * Scenic View,
 * Copyright (C) 2012 Jonathan Giles, Ander Ruiz, Amy Fowler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxconnector.node;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;

import org.fxconnector.Configuration;
import org.scenicview.utils.Logger;

public class SVNodeFactory {
	public static class SVNodeFactoryImpl {
		public void dumpNodeInfo(final StringBuilder sb, final Node node) {
				sb.append("Node:").append(node).append(" Class:").append(node.getClass()).append(" Id:").append(node.getId()).append('\n');
				if (node instanceof Parent) {
					sb.append("Children:").append(((Parent) node).getChildrenUnmodifiable()).append('\n');
				}
			}
		public SVNode createNode(Node node, Configuration configuration, boolean remote) {
				if (remote) {
					/**
					 * This may sound strange but if the node has a parent we create an
					 * SVNode for the parent a we get the correct node latter and if it
					 * has not then we create a normal node
					 */
					final Node parent = node.getParent();
					if (parent != null) {
						final SVRemoteNodeAdapter svparent = new SVRemoteNodeAdapter(parent, configuration.isCollapseControls(), configuration.isCollapseContentControls());
						final List<SVNode> childrens = svparent.getChildren();
						for (int i = 0; i < childrens.size(); i++) {
							if (childrens.get(i).equals(node)) {
								return childrens.get(i);
							}
						}
						final StringBuilder sb = new StringBuilder();
						sb.append("Error while creating node:" + node.getClass() + " id:" + node.getId()).append('\n');

						sb.append("NODE INFORMATION\n");
						dumpNodeInfo(sb, node);
						sb.append("PARENT INFORMATION\n");
						dumpNodeInfo(sb, node.getParent());
						throw new RuntimeException(sb.toString());

					} else {
						return new SVRemoteNodeAdapter(node, configuration.isCollapseControls(), configuration.isCollapseContentControls());
					}
				} else {
					return new SVRealNodeAdapter(node, configuration.isCollapseControls(), configuration.isCollapseContentControls());
				}
			}
	};

	public static SVNodeFactoryImpl INSTANCE;

	static {
		INSTANCE = new SVNodeFactoryImpl();
	}

	private SVNodeFactory() {
		// no-op
	}

	public static SVNode createNode(final Node node, final Configuration configuration, final boolean remote) {
//		Logger.print("Creating node with " + INSTANCE.toString() + " " + node.toString() + " - " + configuration.toString() + " - remote: " + remote);
		SVNode created = INSTANCE.createNode(node, configuration, remote);
//		Logger.print("\tCreated svnode: " + created.toString());
		return created;
	}
}
