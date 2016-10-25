package com.example.yunyao.lab3;

/**
 * Created by yunyao on 2016/10/12.
 */
    public class dataclass {
        private String First_letter;
        private String Name;
        private String Number;
        private String Equipment;
        private String Detail;
        private String Color;

        public dataclass(String First_letter, String Name, String Number, String Equipment, String Detail, String Color) {
            this.First_letter = First_letter;
            this.Name = Name;
            this.Number = Number;
            this.Equipment = Equipment;
            this.Detail = Detail;
            this.Color = Color;
        }
        public String getFirst_letter(){return First_letter;}
        public String getName(){return Name;}
        public String getNumber(){return Number;}
        public String getEquipment(){return Equipment;}
        public String getDetail(){return Detail;}
        public String getColor(){return Color;}

}
