package com.sctjsj.forestcommunity.javabean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lifuy on 2017/9/1.
 */

public class ProvincialCityBean implements Serializable , IPickerViewData {

    private String name;
    private int id;
    private List<CityBean> cityBeanList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CityBean> getCityBeanList() {
        return cityBeanList;
    }

    public void setCityBeanList(List<CityBean> cityBeanList) {
        this.cityBeanList = cityBeanList;
    }

    @Override
    public String toString() {
        return "ProvincialCityBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", cityBeanList=" + cityBeanList +
                '}';
    }

    @Override
    public String getPickerViewText() {
        return this.name;
    }

    public static class CityBean implements Serializable{
        private String name;
        private int id;
        private List<AreaBean> areaBeanList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<AreaBean> getAreaBeanList() {
            return areaBeanList;
        }

        public void setAreaBeanList(List<AreaBean> areaBeanList) {
            this.areaBeanList = areaBeanList;
        }

        @Override
        public String toString() {
            return "CityBean{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    ", areaBeanList=" + areaBeanList +
                    '}';
        }

        public static class AreaBean implements Serializable{
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return "AreaBean{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
