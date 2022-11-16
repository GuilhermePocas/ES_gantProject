package net.sourceforge.ganttproject.task;

public class TaskObjectiveImpl implements TaskObjective{
        private final int id;
        private String name;
        private int percentage;
        private boolean checked;

        public TaskObjectiveImpl(int id, String name, int percentage) {
            this.id = id;
            this.name = name;
            this.percentage = percentage;
            this.checked = false;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getPercentage() {
            return percentage;
        }

        public boolean isChecked() {
            return checked;
        }

        public void check(boolean value) {
            checked =  value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }

}
