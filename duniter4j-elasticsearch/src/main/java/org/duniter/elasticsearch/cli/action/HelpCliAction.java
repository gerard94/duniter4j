package org.duniter.elasticsearch.cli.action;

/*
 * #%L
 * SIH-Adagio :: Shared
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Ifremer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

public class HelpCliAction {

	public void show() {
		StringBuilder sb = new StringBuilder();

		sb.append("Usage: duniter4j-elaticsearch.<sh|bat> <commands> [options]\n\n")
				.append("Commands:\n\n")
				.append(" start                            Start elastic search node\n")
				.append(" index                            Index blocks from BMA Node\n")
				.append(" reset-data                       Reset all indexed data (blocks and records)\n")
				.append(" reset-data-blocks                Reset only indexed blocks (from uCoin node's)\n")
				.append(" reset-data-records               Reset only indexed records\n")
				.append("\n")
				.append("\n")
				.append("Options:\n\n")
				.append(" --help                           Output usage information\n")
				.append(" -h --host <user>		           uCoin node host (with Basic Merkled API)\n")
				.append(" -p --port <pwd> 		           uCoin node port (with Basic Merkled API)\n")
				.append("\n")
				.append(" -esh  --es-host <user>           ElasticSearch node host\n")
				.append(" -esp  --es-port <pwd>            ElasticSearch node port\n");

		System.out.println(sb.toString());
	}
}
