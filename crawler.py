import urllib.request as req

from html.parser import HTMLParser

class FIFO_Policy:
    def __init__(self, c):
        self.queue = [s for s in c.seedURLs]

    def getURL(self, c, iteration):
        if (len(self.queue) == 0):
            self.queue = [s for s in c.seedURLs]
        url = self.queue[0]
        self.queue.remove(url)
        return url

    def updateURLs(self, c, newURLs, newURLsWD, iteration):
        tmpList = [url for url in newURLs]
        tmpList.sort(key=lambda url: url[len(url) - url[::-1].index('/'):])
        self.queue.extend(tmpList)


class Container:
    def __init__(self):
        # The name of the crawler"
        self.crawlerName = "IRbot"
        # Example ID
        self.example = ""
        # Root (host) page
        self.rootPage = "https://us.soccerway.com"
        # Initial links to visit
        self.seedURLs = ["https://us.soccerway.com/teams/club-teams"]
        # Maintained URLs
        self.URLs = set([])
        # Outgoing URLs (from -> list of outgoing links)
        self.outgoingURLs = {}
        # Incoming URLs (to <- from; set of incoming links)
        self.incomingURLs = {}
        # Class which maintains a queue of urls to visit.
        self.generatePolicy = FIFO_Policy(self)
        # Page (URL) to be fetched next
        self.toFetch = None
        # Number of iterations of a crawler.
        self.iterations = 200

        # If true: store all crawled html pages in the provided directory.
        self.storePages = True
        self.storedPagesPath = "./" + self.example + "/pages/"
        # If true: store all discovered URLs (string) in the provided directory
        self.storeURLs = True
        self.storedURLsPath = "/" + self.example + "/urls/"
        # If true: store all discovered links (dictionary of sets: from->set to),
        # for web topology analysis, in the provided directory
        self.storeOutgoingURLs = True
        self.storedOutgoingURLs = "/" + self.example + "/outgoing/"
        # Analogously to outgoing
        self.storeIncomingURLs = True
        self.storedIncomingURLs = "/" + self.example + "/incoming/"

        # If True: debug
        self.debug = False


class Parser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.output_list = []

    def handle_starttag(self, tag, attrs):
        if tag == 'a':
            self.output_list.append(dict(attrs).get('href'))



def main():
    c = Container()
    # Inject: parse seed links into the base of maintained URLs
    inject(c)
    teams_urls=[]

    for iteration in range(c.iterations):

        if c.debug:
            print("=====================================================")
            print("Iteration = " + str(iteration + 1))
            print("=====================================================")

        # Prepare a next page to be fetched
        generate(c, iteration)
        if (c.toFetch == None):
            if c.debug:
                print("   No page to fetch!")
            continue

        # Generate: it downloads html page under "toFetch URL"
        page = fetch(c)

        if page == None:
            if c.debug:
                print("   Unexpected error; skipping this page")
            removeWrongURL(c)
            continue

        # Parse file
        htmlData, newURLs = parse(c, page, iteration)



        ### normalise newURLs
        newURLs = filterURLs(c,newURLs)

        ### update outgoing/incoming links
        updateOutgoingURLs(c, newURLs)
        updateIncomingURLs(c, newURLs)


        ### removeDuplicates
        newURLsWD = removeDuplicates(c, newURLs)

        ### update urls
        c.generatePolicy.updateURLs(c, newURLs, newURLsWD, iteration)
        teams_urls.append(newURLsWD)
        # Add newly obtained URLs to the container
        if c.debug:
            print("   Maintained URLs...")
            for url in c.URLs:
                print("      " + str(url))

        if c.debug:
            print("   Newly obtained URLs (duplicates with maintaines URLs possible) ...")
            for url in newURLs:
                print("      " + str(url))
        if c.debug:
            print("   Newly obtained URLs (without duplicates) ...")
            for url in newURLsWD:
                print("      " + str(url))
            for url in newURLsWD:
                c.URLs.add(url)



    print(teams_urls)



# Inject seed URL into a queue
def inject(c):
    for l in c.seedURLs:
        if c.debug:
            print("Injecting " + str(l))
        c.URLs.add(l)


# Produce next URL to be fetched
def generate(c, iteration):
    url = c.generatePolicy.getURL(c, iteration)
    if url == None:
        if c.debug:
            print("   Fetch: error")
        c.toFetch = None
        return None

    print("   Next page to be fetched = " + str(url))
    c.toFetch = url


# Generate (download html) page
def fetch(c):
    URL = c.toFetch
    if c.debug:
        print("   Downloading " + str(URL))
    try:
        opener = req.build_opener()
        opener.addheadders = [('User-Agent', c.crawlerName)]
        webPage = opener.open(URL)
        return webPage
    except:
        return None



# Remove wrong URL
def removeWrongURL(c):
    if (c.toFetch in c.URLs):
        c.URLs.remove(c.toFetch)


# Parse this page and retrieve text (whole page) and URLs
def parse(c, page, iteration):
    # data to be saved (DONE)
    htmlData = page.read()
    # obtained URLs
    p = Parser()
    p.feed(str(htmlData))
    newURLs = set([s for s in p.output_list])
    if c.debug:
        print("   Extracted " + str(len(newURLs)) + " links")

    return htmlData, newURLs


# Filter and normalise obtained urls
def filterURLs(c,newURLs):
    urls=[]
    for url in newURLs:
        if url!=None:
            if(url.startswith("/")):
                urls.append(c.rootPage+url.lower())
    toLeft = set([url.lower() for url in urls])
    return toLeft


# Remove duplicates (duplicates)
def removeDuplicates(c, newURLs):
    toLeft = set([url for url in newURLs if url not in c.URLs])
    if c.debug:
        print("Removed " + str(len(newURLs) - len(toLeft)) + " urls")
    return toLeft




# Update outgoing links
def updateOutgoingURLs(c, newURLsWD):
    if c.toFetch not in c.outgoingURLs:
        c.outgoingURLs[c.toFetch] = set([])
    for url in newURLsWD:
        c.outgoingURLs[c.toFetch].add(url)


# Update incoming links
def updateIncomingURLs(c, newURLsWD):
    for url in newURLsWD:
        if url not in c.incomingURLs:
            c.incomingURLs[url] = set([])
        c.incomingURLs[url].add(c.toFetch)






if __name__ == "__main__":
    main()
