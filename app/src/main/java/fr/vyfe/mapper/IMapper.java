package fr.vyfe.mapper;


import java.util.HashMap;
import java.util.List;

public interface IMapper<From, To> {

    To map(From from, String key);

    List<To> mapList(HashMap<String, From> from);

    From unMap(To to);

    HashMap<String, From> unMapList(List<To> to);

}
