package com.zer0s2m.creeptenuous.module.search;

import java.util.List;

public interface ServiceSearchFileObject {

    ServiceSearchFileObject setSystemParents(List<String> systemParents);

    List<String> getSystemParents(List<String> systemParents);

    ServiceSearchFileObject setUserLogin(String userLogin);

    String getUserLogin();

    ServiceSearchFileObject setPartRealName(String partRealName);

    String setPartRealName();

    ServiceSearchFileObject setTypeSearchFileObject(SearchFileObject searchFileObject);

    SearchFileObject getTypeSearchFileObject();

    List<ContainerInfoSearchFileObject> search();

}
