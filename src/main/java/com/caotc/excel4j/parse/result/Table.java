package com.caotc.excel4j.parse.result;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Sheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caotc.excel4j.config.MenuConfig;
import com.caotc.excel4j.config.TableConfig;
import com.caotc.excel4j.parse.error.TableError;
import com.caotc.excel4j.util.ExcelUtil;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;

public class Table<V> {
  public static class Builder<V> {
    private TableConfig<V> tableConfig;
    private SheetParseResult sheetParseResult;
    private List<TableError> errors;

    public Table<V> build() {
      return new Table<>(this);
    }

    public TableConfig<V> getTableConfig() {
      return tableConfig;
    }

    public Builder<V> setTableConfig(TableConfig<V> tableConfig) {
      this.tableConfig = tableConfig;
      return this;
    }

    public SheetParseResult getSheetParseResult() {
      return sheetParseResult;
    }

    public Builder<V> setSheetParseResult(SheetParseResult sheetParseResult) {
      this.sheetParseResult = sheetParseResult;
      return this;
    }

    public List<TableError> getErrors() {
      return errors;
    }

    public Builder<V> setErrors(List<TableError> errors) {
      this.errors = errors;
      return this;
    }
  }

  private final Traverser<Menu<?>> MENU_TRAVERSER =
      Traverser.forTree(new SuccessorsFunction<Menu<?>>() {
        @Override
        public Iterable<? extends Menu<?>> successors(Menu<?> node) {
          return node.getChildrens();
        }
      });

  private final Function<MenuConfig<?>, String> MENU_CONFIG_NO_MATCH_MESSAGE_FUNCTION =
      config -> config + "don't have any matches cell";

  public static <V> Builder<V> builder() {
    return new Builder<>();
  }

  private final TableConfig<V> tableConfig;
  private final ImmutableList<TableError> errors;
  private final SheetParseResult sheetParseResult;
  private final ImmutableCollection<Menu<?>> topMenus;

  public Table(Builder<V> builder) {
    tableConfig = builder.tableConfig;
    sheetParseResult = builder.sheetParseResult;
    topMenus = loadTopMenus().map(Menu.Builder::build).collect(ImmutableSet.toImmutableSet());

    // new TableError(this, tableConfig.getMatcher().getMessageFunction().apply(this));
    // TODO 顺序问题?
    ImmutableCollection<MenuConfig<?>> matchesMenuConfigs =
        topMenus.stream().map(Menu::getMenuConfig).collect(ImmutableSet.toImmutableSet());

    tableConfig.getDataConfig().getConstructType();
    
    errors = tableConfig.getTopMenuConfigs().stream()
        .filter(config -> !matchesMenuConfigs.contains(config))
        .map(config -> new TableError(this, MENU_CONFIG_NO_MATCH_MESSAGE_FUNCTION.apply(config)))
        .collect(ImmutableList.toImmutableList());
  }

  private Stream<Menu.Builder<?>> loadTopMenus() {
    ImmutableCollection<MenuConfig<?>> menuConfigs = tableConfig.getTopMenuConfigs();
    Sheet sheet = sheetParseResult.getSheet();
    return ExcelUtil.getCells(sheet).map(StandardCell::valueOf).map(cell -> {
      // TODO 重复匹配问题
      Optional<MenuConfig<?>> optional = menuConfigs.stream()
          .filter(menuConfig -> menuConfig.getMenuMatcher().test(cell)).findAny();
      // TODO safe
      return optional.map(t -> new Menu.Builder().setCell(cell).setMenuConfig(t).setTable(this));
    }).filter(Optional::isPresent).map(Optional::get);
  }

//  public <T> T get(Class<T> type) {
//    Optional<T> optional = tableConfig.getEffectiveParserConfig().newInstance(type);
//    Preconditions.checkArgument(optional.isPresent());
//    topMenus.forEach(menu -> menu.getData().setFieldValue(optional.get()));
//    return optional.get();
//  }
  
  public void getDataMap() {
    
  }
  
  public JSONObject getJSONObjectData() {
    Map<String,Object> map=getDataMenus().collect(Collectors.toMap(Menu::getName, menu->menu.getData().getCellValues()));
    return new JSONObject(map);
  }
  
//  public JSONArray getJSONArrayData() {
//    Map<String,Object> map=getDataMenus().collect(Collectors.toMap(Menu::getName, menu->menu.getData().getCellValues()));
//    return new JSONObject(map);
//  }
  
  public Optional<Menu<?>> findMenu(String menuName) {
    return getMenus().filter(menu -> menu.getName().equals(menuName)).findAny();
  }

  public Stream<Menu<?>> getMenus() {
    return topMenus.stream().map(MENU_TRAVERSER::breadthFirst).flatMap(Streams::stream);
  }

  public Stream<Menu<?>> getDataMenus() {
    return getMenus().filter(Menu::isDataMenu);
  }

//  public Stream<Menu<?>> getFixedDataMenus() {
//    return getDataMenus().filter(Menu::isFixedDataMenu);
//  }

  public Stream<Menu<?>> getFixedDataMenus() {
    return getDataMenus().filter(Menu::isSingleDataMenu);
  }
  
  public Stream<Menu<?>> getUnFixedDataMenus() {
    return getDataMenus().filter(Menu::isUnFixedDataMenu);
  }

  public Stream<Menu<?>> getMustMenus() {
    return getMenus().filter(Menu::isMustMenu);
  }

  public Stream<Menu<?>> getNotMustMenus() {
    return getMenus().filter(Menu::isNotMustMenu);
  }

  public TableConfig<V> getTableConfig() {
    return tableConfig;
  }

  public ImmutableList<TableError> getErrors() {
    return errors;
  }

  public SheetParseResult getSheetParseResult() {
    return sheetParseResult;
  }

  public ImmutableCollection<Menu<?>> getTopMenus() {
    return topMenus;
  }

}
