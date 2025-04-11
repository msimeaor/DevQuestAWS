CREATE TABLE question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  correct_answer VARCHAR(1),
  created_at DATETIME(6),
  difficulty VARCHAR(255),
  justification TEXT,
  technology VARCHAR(255),
  text TEXT,
  CONSTRAINT question_difficulty_check CHECK (difficulty IN ('BASICO', 'INTERMEDIARIO', 'AVANCADO')),
  CONSTRAINT question_technology_check CHECK (
    technology IN (
      'JAVA', 'JAVASCRIPT', 'PYTHON', 'C_SHARP', 'PHP', 'C_PLUS_PLUS', 'RUBY', 'KOTLIN', 'SWIFT', 'GO', 'RUST',
      'SCALA', 'PERL', 'TYPESCRIPT', 'SQL', 'HTML', 'CSS', 'BASH', 'SHELL', 'POWERSHELL', 'MATLAB', 'HASKELL',
      'LUA', 'OBJECTIVE_C', 'DART', 'GROOVY', 'ASSEMBLY', 'FORTRAN', 'COBOL', 'R', 'VHDL', 'VERILOG', 'JSON',
      'XML', 'YAML', 'DOCKER', 'KUBERNETES', 'ANSIBLE', 'CHEF', 'PUPPET', 'TERRAFORM', 'AWS', 'AZURE', 'GCP',
      'SPRING', 'SPRING_BOOT', 'HIBERNATE', 'JPA', 'NODE_JS', 'REACT', 'ANGULAR', 'VUE_JS', 'NEXT_JS', 'NEST_JS',
      'FLUTTER', 'REACT_NATIVE', 'IONIC', 'DJANGO', 'FLASK', 'RUBY_ON_RAILS', 'LARAVEL', 'CODEIGNITER', 'EXPRESS',
      'ASP_NET', 'UNITY', 'UNREAL_ENGINE', 'BLENDER', 'GODOT', 'CRYENGINE', 'OPENCV', 'TENSORFLOW', 'PYTORCH',
      'SCIKIT_LEARN', 'NUMPY', 'PANDAS', 'MATPLOTLIB', 'SEABORN', 'KERAS', 'OPENAI', 'HUGGING_FACE',
      'ELASTICSEARCH', 'KIBANA', 'LOGSTASH', 'APACHE_SPARK', 'HADOOP', 'BIGQUERY', 'TABLEAU', 'POWER_BI',
      'QLIKVIEW', 'LOOKER', 'MYSQL', 'POSTGRESQL', 'MONGODB', 'SQLITE', 'ORACLE', 'SQL_SERVER', 'REDIS',
      'CASSANDRA', 'COUCHDB', 'COUCHBASE', 'INFLUXDB', 'GRAPHQL', 'REST_API', 'SOAP', 'GRPC', 'WEBPACK',
      'BABEL', 'ROLLUP', 'PARCEL', 'SASS', 'LESS', 'TAILWIND_CSS', 'BOOTSTRAP', 'MATERIAL_UI', 'ANT_DESIGN',
      'FOUNDATION', 'ZURB', 'ECLIPSE', 'INTELLIJ', 'VISUAL_STUDIO', 'VS_CODE', 'ANDROID_STUDIO', 'XCODE', 'JIRA',
      'CONFLUENCE', 'SLACK', 'TRELLO', 'NOTION', 'GIT', 'GITHUB', 'GITLAB', 'BITBUCKET', 'SVN', 'MERCURIAL',
      'FIGMA', 'ADOBE_XD', 'SKETCH', 'ZEPLIN', 'INVISION', 'CI_CD', 'JENKINS', 'TRAVIS_CI', 'CIRCLE_CI',
      'GITHUB_ACTIONS', 'BITRISE', 'ARGO_CD', 'PROMETHEUS', 'GRAFANA', 'ZABBIX', 'NAGIOS', 'SPLUNK', 'NEW_RELIC',
      'POSTMAN', 'SOAP_UI', 'JMETER', 'SELENIUM', 'APPIUM', 'CYPRESS', 'PLAYWRIGHT', 'TESTNG', 'JUNIT', 'MOCHA',
      'CHAI', 'JEST', 'CUCUMBER', 'KARMA', 'PROTRACTOR', 'WEB3_JS', 'ETHERS_JS', 'SOLIDITY', 'TRUFFLE', 'HARDHAT',
      'GANACHE', 'METAMASK', 'VERCEL', 'NETLIFY', 'HEROKU', 'DIGITAL_OCEAN', 'LINODE', 'VULTR', 'FIREBASE',
      'SUPABASE', 'BACK4APP', 'STRAPI', 'CONTENTFUL', 'WORDPRESS', 'DRUPAL', 'JOOMLA', 'MAGENTO', 'SHOPIFY',
      'REST', 'SOA', 'OAUTH2', 'OPENID_CONNECT', 'API_GATEWAY', 'SWAGGER'
    )
  )
);
