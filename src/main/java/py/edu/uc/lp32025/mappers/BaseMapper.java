package py.edu.uc.lp32025.mappers;

public interface BaseMapper<E, D> {
    D toDto(E entity);
}
