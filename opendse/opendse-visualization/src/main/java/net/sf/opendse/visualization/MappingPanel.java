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
package net.sf.opendse.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.GraphPanelFormatApplication.FunctionTask;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

public class MappingPanel extends JPanel implements ElementSelectionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	protected final ElementSelection selection;
	protected final EventList<Mapping<Task, Resource>> mappings = new BasicEventList<Mapping<Task, Resource>>();

	protected final JTable table;
	protected final DefaultEventTableModel<Mapping<Task, Resource>> model;
	protected final DefaultEventSelectionModel<Mapping<Task, Resource>> selectionModel;

	class MappingTableFormat implements AdvancedTableFormat<Mapping<Task, Resource>> {

		protected String[] columnNames = { "Mapping", "Task", "Resource", "VOID" };

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Object getColumnValue(Mapping<Task, Resource> mapping, int column) {
			switch (column) {
			case 0:
				return "" + mapping.getId();
			case 1:
				return "" + mapping.getSource();
			case 2:
				return "" + mapping.getTarget();
			default:
				return "VOID";
			}
		}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

		@Override
		public Comparator<?> getColumnComparator(int arg0) {
			return new AdvancedStringComparator<Object>();
		}
	}

	protected class AdvancedStringComparator<E> implements Comparator<E> {
		@Override
		public int compare(E e1, E e2) {
			String o1 = e1.toString();
			String o2 = e2.toString();
			String[] s1 = sep(o1);
			String[] s2 = sep(o2);

			int c = s1[0].compareTo(s2[0]);
			if (c != 0) {
				return c;
			} else {
				Double i1 = toDigit(s1);
				Double i2 = toDigit(s2);
				return i1.compareTo(i2);
			}
		}

		protected Double toDigit(String[] s) {
			if (s[1].length() > 0) {
				return Double.parseDouble(s[1]);
			} else {
				return 0d;
			}
		}

		protected String[] sep(String o) {
			String s = "";
			String d = "";
			for (char c : o.toCharArray()) {
				if (!Character.isDigit(c) || (c == '.' && !d.contains("."))) {
					s += c;
				} else {
					d += c;
				}
			}
			if (d.startsWith(".")) {
				d = "0" + d;
			}
			if (d.endsWith(".")) {
				d += "0";
			}

			return new String[] { s, d };
		}
	}

	class TableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Mapping<Task, Resource> mapping = model.getElementAt(row);

			if (!selection.isNull()) {
				Object sel = selection.get();
				if (sel instanceof FunctionTask) {
					boolean contain = ((FunctionTask) sel).getFunction().containsVertex(mapping.getSource());
					c.setForeground(contain ? Color.BLACK : Color.GRAY.brighter());
				} else if (mapping.equals(sel) || mapping.getSource().equals(sel) || mapping.getTarget().equals(sel)) {
					c.setForeground(Color.BLACK);
				} else {
					c.setForeground(Color.GRAY.brighter());
				}
			} else {
				c.setForeground(Color.BLACK);
			}

			setToolTipText(ViewUtil.getTooltip(mapping));
			return c;
		}

	}

	public MappingPanel(Mappings<Task, Resource> mappings, ElementSelection selection) {
		for (Mapping<Task, Resource> mapping : mappings) {
			this.mappings.add(mapping);
		}
		this.selection = selection;
		this.selection.addListener(this);

		SortedList<Mapping<Task, Resource>> sortedMappings = new SortedList<Mapping<Task, Resource>>(this.mappings,
				new AdvancedStringComparator<Mapping<Task, Resource>>());

		setLayout(new BorderLayout());

		model = new DefaultEventTableModel<Mapping<Task, Resource>>(sortedMappings, new MappingTableFormat());

		selectionModel = new DefaultEventSelectionModel<Mapping<Task, Resource>>(sortedMappings);
		selectionModel.setSelectionMode(DefaultEventSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(this);

		table = new JTable(model);
		table.setSelectionModel(selectionModel);
		table.setDefaultRenderer(Object.class, new TableCellRenderer());
		TableComparatorChooser.install(table, sortedMappings, TableComparatorChooser.SINGLE_COLUMN);

		JScrollPane scroll = new JScrollPane(table);
		add(scroll);

		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON3) {
					MappingPanel.this.selection.set(null);
				}
			}
		});
	}

	public void selectionChanged(ElementSelection selection) {
		if (!(selection.get() instanceof Mapping)) {
			selectionModel.removeSelectionInterval(selectionModel.getMinSelectionIndex(),
					selectionModel.getMaxSelectionIndex());
		}

		repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			List<Mapping<Task, Resource>> list = selectionModel.getSelected();

			if (!list.isEmpty()) {
				Mapping<Task, Resource> mapping = list.get(0);
				selection.set(mapping);
			}
		}
	}

}
