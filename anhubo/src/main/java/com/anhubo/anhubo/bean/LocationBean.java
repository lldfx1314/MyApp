package com.anhubo.anhubo.bean;

<<<<<<< HEAD
import java.io.Serializable;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import java.util.List;

/**
 * Created by LUOLI on 2016/11/21.
 */
public class LocationBean extends BaseEntity {
    public Data data;

    public class Data {

        public int page;
<<<<<<< HEAD
        public List<Business> business;

        public List<Building> building;

        public class Business implements Serializable {

            public String name;
            public String poi_id;

        }

        public class Building implements Serializable {

            public String name;
            public String poi_id;

        }
=======
        public List<String> business;
        public List<String> building;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }
}
