package edu.univ1.service;
import edu.univ1.api.*;
import edu.univ1.auth.*;
import edu.univ1.data.*;
import edu.univ1.domain.*;
import edu.univ1.util.*;
import edu.univ1.ui.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryService{
    private final UserAuthDAO userAuthDAO=new UserAuthDAO();
    public Map<Long,String> getUsernames(List<Long> userIds){
        if(userIds==null||userIds.isEmpty()){
            return Collections.emptyMap();
        }
        return userAuthDAO.getUsernamesByIds(userIds);
    }
}