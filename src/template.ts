interface TemplateParams {
  body: string;
  title: string;
}

function template({ body, title }: TemplateParams) {
  return [
    "html",
    [
      "head",
      ["meta", { charset: "utf-8" }],
      ["title", title],
      ["link", { rel: "stylesheet", href: "/css/style.css" }],
      ["link", { rel: "stylesheet", href: "/vendor/basscss@8.0.1.min.css" }],
      ["link", { rel: "stylesheet", href: "/vendor/highlight.css" }],
      [
        "link",
        {
          rel: "stylesheet",
          href: "https://fonts.googleapis.com/css?family=Josefin+Sans"
        }
      ],
      "<script>hljs.initHighlightingOnLoad();</script><script>(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n\n  ga('create', 'UA-81846608-1', 'auto');\n  ga('send', 'pageview');</script>",
      '<script src="/vendor/highlight.js" type="text/javascript"></script>',
      "<script> hljs.initHighlightingOnLoad(); </script>"
    ],
    [
      "body",
      [
        "div.content.mx-auto",
        [
          "div.clearfix",
          [
            "div.header",
            ["a", { href: "/" }, "presumably for side-effects"],
            ["br"],
            "a blog about programming"
          ],
          ["div", body]
        ],
        ["hr.rule"],
        [
          "p.m2",
          "This is ",
          ["i", "presumably for side-effects"],
          ", a blog by Paulus Esterhazy. Don't forget to say hello ",
          ["a", { href: "https://twitter.com/pesterhazy" }, "on twitter"],
          " or ",
          ["a", { href: "mailto:pesterhazy@gmail.com" }, "by email"]
        ]
      ]
    ]
  ];
}

export { template };
