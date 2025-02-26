/*
GanttProject is an opensource project management tool.
Copyright (C) 2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.chart.gantt;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Date;

import com.google.common.base.Strings;

import biz.ganttproject.core.calendar.CalendarEvent;
import biz.ganttproject.core.time.CalendarFactory;
import net.sourceforge.ganttproject.ChartComponentBase;
import net.sourceforge.ganttproject.GanttGraphicArea;
import net.sourceforge.ganttproject.chart.ChartModelImpl;
import net.sourceforge.ganttproject.chart.item.*;
import net.sourceforge.ganttproject.chart.mouse.MouseMotionListenerBase;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskObjectiveCollection;

class MouseMotionListenerImpl extends MouseMotionListenerBase {
  private final ChartComponentBase myChartComponent;
  private GanttChartController myChartController;

  public MouseMotionListenerImpl(GanttChartController chartImplementation, ChartModelImpl chartModel,
      UIFacade uiFacade, ChartComponentBase chartComponent) {
    super(uiFacade, chartImplementation);
    myChartController = chartImplementation;
    myChartComponent = chartComponent;
  }

  // Move the move on the area
  @Override
  public void mouseMoved(MouseEvent e) {
    ChartItem itemUnderPoint = myChartController.getChartItemUnderMousePoint(e.getX(), e.getY());
    Task taskUnderPoint = itemUnderPoint == null ? null : itemUnderPoint.getTask();
    // System.err.println("[OldMouseMotionListenerImpl] mouseMoved:
    // taskUnderPoint="+taskUnderPoint);
    myChartController.hideTooltip();
    if (taskUnderPoint == null) {
      myChartComponent.setDefaultCursor();

      if (itemUnderPoint instanceof CalendarChartItem) {
        CalendarEvent event = findCalendarEvent(((CalendarChartItem) itemUnderPoint).getDate());
        if (event != null) {
          String tooltipText;
          if (event.isRecurring) {
            tooltipText = GanttLanguage.getInstance().formatText(
                "timeline.holidayTooltipRecurring.pattern",
                GanttLanguage.getInstance().getRecurringDateFormat().format(event.myDate),
                Strings.nullToEmpty(event.getTitle()));
          } else {
            tooltipText = GanttLanguage.getInstance().formatText(
                "timeline.holidayTooltip.pattern",
                GanttLanguage.getInstance().formatDate(CalendarFactory.createGanttCalendar(event.myDate)),
                Strings.nullToEmpty(event.getTitle()));
          }
          myChartController.showTooltip(e.getX(), e.getY(), tooltipText);
        }
      }
    }
    else if (itemUnderPoint instanceof TaskBoundaryChartItem) {
      Cursor cursor = ((TaskBoundaryChartItem) itemUnderPoint).isStartBoundary() ? GanttGraphicArea.W_RESIZE_CURSOR
          : GanttGraphicArea.E_RESIZE_CURSOR;
      myChartComponent.setCursor(cursor);
    }
    // special cursor
    else if (itemUnderPoint instanceof TaskProgressChartItem) {
      myChartComponent.setCursor(GanttGraphicArea.CHANGE_PROGRESS_CURSOR);
    }
    else if (itemUnderPoint instanceof TaskNotesChartItem && taskUnderPoint.getNotes() != null) {
      myChartComponent.setCursor(ChartComponentBase.HAND_CURSOR);
      myChartController.showTooltip(e.getX(), e.getY(),
          GanttLanguage.getInstance().formatText(
              "task.notesTooltip.pattern",taskUnderPoint.getNotes().replace("\n", "<br>")));
    }
    else if(itemUnderPoint instanceof TaskRegularAreaChartItem && taskUnderPoint.getObjectivesCollection().size()!=0){
      myChartComponent.setCursor(ChartComponentBase.HAND_CURSOR);
      TaskObjectiveCollection objCole = taskUnderPoint.getObjectivesCollection();
      String textObjectives = "";
      for(int i = 0; i < objCole.size(); i++) {
        if (!objCole.get(i).isChecked()) {
          String redObjectives = Integer.toString(objCole.get(i).getId()).concat(" - ").concat(objCole.get(i).getName()).concat(" - ").concat(Integer.toString(objCole.get(i).getPercentage())).concat("%").concat("\n");
          textObjectives = textObjectives.concat(String.format("<html><font color='%s'>%s<html>", "#B22222", redObjectives));
        } else {
          String greenObjectives = Integer.toString(objCole.get(i).getId()).concat(" - ").concat(objCole.get(i).getName()).concat(" - ").concat(Integer.toString(objCole.get(i).getPercentage())).concat("%").concat("\n");
          textObjectives = textObjectives.concat(String.format("<html><font color='%s'>%s<html>", "#008000", greenObjectives));
        }

      }

        myChartController.showTooltip(e.getX(), e.getY()
              ,GanttLanguage.getInstance().formatText(
                      "task.objectivesTooltip.pattern",textObjectives.replace("\n","<br>")));
    }
    else {
      myChartComponent.setCursor(ChartComponentBase.HAND_CURSOR);
    }

  }

  private CalendarEvent findCalendarEvent(Date date) {
    return myChartComponent.getProject().getActiveCalendar().getEvent(date);
  }
}