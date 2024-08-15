package camp.woowak.lab.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.menu.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
