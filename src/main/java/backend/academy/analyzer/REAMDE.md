#### Тестовые конфигурации:

```
analyzer --path logs.txt --format markdown --filter-field userAgent --filter-value "Mozilla"
analyzer --path logs.txt --format markdown --filter-field method --filter-value "GET"
analyzer --path https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs --format adoc
analyzer --path logs.txt --to 2024-10-26 --format markdown
analyzer --path logs.txt --from 2024-10-26 --format adoc
analyzer --path logs.txt --from 2024-10-27 --to 2024-10-27 --format adoc
analyzer --path logs/**/logs.txt --format markdown
```

#### Доступные фильтры

`--path`
`--from`
`--to`
`--format`
`--filter-field`
`--filter-value`

#### Доступные метрики

`getBytesList`
`getFromDate`
`getToDate`
`getTotalCountRequest`
`getFrequentlyRequestedResources`
`getFrequentlyRequestedCode`
`getAverageSizeServerResponse`
`getPercentile`
`getUniqueClientsCount`
`getMostFrequentMethod`

#### Доступные форматы

`markdown`
`adoc`
