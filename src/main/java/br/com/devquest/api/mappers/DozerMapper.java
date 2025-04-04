package br.com.devquest.api.mappers;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class DozerMapper {

  private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

  public static <O, D> D parseObject(O origin, Class<D> destination) {
    return mapper.map(origin, destination);
  }

  public static <O, D> List<D> parseListObjects(List<O> originList, Class<D> destination) {
    List<D> destinationList = new ArrayList<>();
    originList.forEach(originObject -> {
      destinationList.add(mapper.map(originObject, destination));
    });

    return destinationList;
  }

}
