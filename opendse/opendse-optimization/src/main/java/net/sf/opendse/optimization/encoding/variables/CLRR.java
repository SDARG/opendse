/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CLRR extends Variable implements CommunicationVariable {

	public CLRR(Task t, Edge l, Resource r0, Resource r1) {
		super(t,l,r0,r1);
	}

	public Task getTask() {
		return get(0);
	}

	public Edge getLink() {
		return get(1);
	}

	public Resource getSource() {
		return get(2);
	}

	public Resource getDest() {
		return get(3);
	}
	
	@Override
	public ICommunication getCommunication() {
		return (ICommunication)getTask();
	}
}
